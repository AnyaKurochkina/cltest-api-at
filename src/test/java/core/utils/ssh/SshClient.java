package core.utils.ssh;

import com.jcraft.jsch.*;
import core.helper.Configure;
import core.helper.StringUtils;
import core.utils.Waiting;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Objects;

@Log4j2
@Builder
public class SshClient {
    @Builder.Default
    private int port = 22;
    @Builder.Default
    private int timeout = 10000;
    @Getter
    private String host;
    private String user;
    private String password;
    private String privateKey;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public static class SshClientBuilder {
        public SshClientBuilder user(String user) {
            this.user = Objects.requireNonNull(user, "Не задан User");
            return this;
        }

        public SshClientBuilder env(String env) {
            Assumptions.assumeTrue("dev".equalsIgnoreCase(env), "Тест включен только для dev среды");
            this.user = Objects.requireNonNull(Configure.getAppProp(env + ".user"), "Не задан параметр " + env + ".user");
            this.password = Objects.requireNonNull(Configure.getAppProp(env + ".password"), "Не задан параметр " + env + ".password");
            return this;
        }
    }

    @SneakyThrows
    public String execute(String cmd, Object ... args) {
        try {
            Channel channel = initChannel(StringUtils.format(cmd, args), initSession());
            channel.connect();
            if (!Waiting.sleep(channel::isClosed, Duration.ofMinutes(1)))
                log.debug("SSH Соединение будет закрыто принудительно");
            String res = out.toString().replaceAll("[ \t\r\n]+$", "");
            log.debug("SSH response: '{}'", res);
            return res;
        } catch (JSchException e){
            log.debug("SSH connect error: {} {} {} {}", host, user, password, privateKey);
            throw e;
        }
    }

    public void writeTextFile(String path, String text){
        Assertions.assertEquals("", execute("echo '{}' > {}", escapeShell(text), path));
    }

    private String escapeShell(String input) {
        return input.replace("'", "'\\''");
    }

    private Channel initChannel(String commands, Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(commands);
        out.reset();
        channelExec.setOutputStream(out);
        channelExec.setErrStream(out);
        return channel;
    }

    private Session initSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        if(Objects.nonNull(privateKey))
            jsch.addIdentity(privateKey);
        session.setPassword(password);
        UserInfo userInfo = new SshUserInfo();
        session.setUserInfo(userInfo);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.connect(timeout);
        return session;
    }
}
