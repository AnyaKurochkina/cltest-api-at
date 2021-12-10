package core.utils.ssh;

import com.jcraft.jsch.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class SshClient {
    private static final int SSH_PORT = 22;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024;
    private final String host;
    private final String user;
    private final String password;

    public SshClient(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }


    public void execCommandToList(String command) {
        List<String> lines = connectAndExecuteListCommand(command);
        log.debug("{}", lines);
    }


    public String execCommandToString(String command, int index) {
        List<String> lines = connectAndExecuteListCommand(command);
        log.debug(lines.get(index));
        return lines.get(index);
    }


    @SneakyThrows
    public List<String> connectAndExecuteListCommand(String cmd) {
        List<String> lines;
        Session session = initSession(host, user, password);
        Channel channel = initChannel(cmd, session);
        InputStream in = channel.getInputStream();
        channel.connect();
        log.info("Соединение установлено...");
        String dataFromChannel = getDataFromChannel(channel, in);
        lines = new ArrayList<>(Arrays.asList(dataFromChannel.split("\n")));
        channel.disconnect();
        session.disconnect();
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
