package clp.core.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.exception.CustomException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SshClient {

    private static final int SSH_PORT = 22;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024;

    private static final Logger log = LoggerFactory.getLogger(SshClient.class);
    private String host;
    private String user;
    private String password;

    private SshClient() {
    }

    public SshClient(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }


    public void execCommandToList(String command) throws CustomException {
        SshClient manager = new SshClient();
        List<String> lines = manager.connectAndExecuteListCommand(this.host, this.user, this.password, command);
        log.debug("{}", lines);
    }


    public String execCommandToString(String command, int index) throws CustomException {
        SshClient manager = new SshClient();
        List<String> lines = manager.connectAndExecuteListCommand(this.host, this.user, this.password, command);
        log.debug(lines.get(index));
        return lines.get(index);
    }


    // Выполняем коннект, предаём поманду, получаем ответ
    public List<String> connectAndExecuteListCommand(String host, String username, String password, String cmd) throws CustomException {
        List<String> lines = new ArrayList<>();
        try {
            String command = cmd;
            Session session = initSession(host, username, password);
            Channel channel = initChannel(command, session);
            InputStream in = channel.getInputStream();
            channel.connect();
            log.info("Соединение установлено...");
            String dataFromChannel = getDataFromChannel(channel, in);
            lines.addAll(Arrays.asList(dataFromChannel.split("\n")));
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(e);
        }
        return lines;
    }

    private String getDataFromChannel(Channel channel, InputStream in)
            throws IOException {
        StringBuilder result = new StringBuilder();
        byte[] tmp = new byte[BUFFER_SIZE];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                result.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                int exitStatus = channel.getExitStatus();
                log.info("exit-status: {}", exitStatus);
                break;
            }
        }
        return result.toString();
    }

    private Channel initChannel(String commands, Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(commands);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        return channel;
    }


    private Session initSession(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, SSH_PORT);
        session.setPassword(password);
        UserInfo userInfo = new SshUserInfo();
        session.setUserInfo(userInfo);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(CONNECTION_TIMEOUT);
        return session;
    }
}