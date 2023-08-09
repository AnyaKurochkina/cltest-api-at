package core.utils.ssh;

import com.jcraft.jsch.*;
import core.helper.Configure;
import core.utils.Waiting;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assumptions;

import java.io.*;
import java.time.Duration;
import java.util.Objects;

@Log4j2
public class SshClient {
    private static final int SSH_PORT = 22;
    private static final int CONNECTION_TIMEOUT = 10000;
    private final String host;
    private final String user;
    private final String password;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public SshClient(String host, String user, String password) {
        this.host = host;
        this.user = Objects.requireNonNull(user, "Не задан");
        this.password = password;
    }

    public SshClient(String host, String env) {
        Assumptions.assumeTrue("dev".equalsIgnoreCase(env), "Тест включен только для dev среды");
        this.host = host;
        this.user = Objects.requireNonNull(Configure.getAppProp(env + ".user"),
                "Не задан параметр " + env + ".user");
        this.password = Objects.requireNonNull(Configure.getAppProp(env + ".password"),
                "Не задан параметр " + env + ".password");
    }

    @SneakyThrows
    public String execute(String cmd) {
        try {
            Session session = initSession(host, user, password);
            Channel channel = initChannel(cmd, session);
            channel.connect();
            if (!Waiting.sleep(channel::isClosed, Duration.ofMinutes(1)))
                log.debug("SSH Соединение будет закрыто принудительно");
            String res = out.toString();
            log.debug("SSH response: {}", res);
            return res;
        } catch (JSchException e){
            log.debug("SSH connect error: {} {} {}", host, user, password);
            throw e;
        }
    }

    private Channel initChannel(String commands, Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(commands);
        channelExec.setOutputStream(out);
        channelExec.setErrStream(out);
        return channel;
    }

    private Session initSession(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, SSH_PORT);
        session.setPassword(password);
        UserInfo userInfo = new SshUserInfo();
        session.setUserInfo(userInfo);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.connect(CONNECTION_TIMEOUT);
        return session;
    }
}
