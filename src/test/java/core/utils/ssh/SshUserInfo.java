package core.utils.ssh;

import com.jcraft.jsch.UserInfo;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshUserInfo implements UserInfo {
    private String password;
    public void showMessage(String message) {
        log.info(message);
    }

    public boolean promptYesNo(String message) {
        log.info(message);
        return true;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean promptPassphrase(String arg0) {
        log.info(arg0);
        return true;
    }

    @Override
    public boolean promptPassword(String arg0) {
        log.info(arg0);
        this.password = arg0;
        return true;
    }
}
