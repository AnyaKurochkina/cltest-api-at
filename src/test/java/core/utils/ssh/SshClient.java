package core.utils.ssh;

import com.jcraft.jsch.*;
import core.helper.Configure;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
        this.user = Objects.requireNonNull(user, "Не задан");
        this.password = password;
    }

    public SshClient(String host, String env) {
        this.host = host;
        this.user = Objects.requireNonNull(Configure.getAppProp(env + ".user"),
                "Не задан параметр " + env + ".user");
        this.password = Objects.requireNonNull(Configure.getAppProp(env + ".password"),
                "Не задан параметр " + env + ".password");
        ;
    }


    @SneakyThrows
    public String execute(String cmd) {
        Session session = initSession(host, user, password);
        Channel channel = initChannel(cmd, session);
        InputStream in = channel.getInputStream();
        channel.connect();
        log.info("Соединение установлено...");
        String dataFromChannel = getDataFromChannel(channel, in);
        channel.disconnect();
        session.disconnect();
        return dataFromChannel;
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
