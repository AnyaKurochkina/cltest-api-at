package core.utils;

import core.helper.Configure;

public class EnviromentChecker {
    public static Boolean standEnvIs(Env stand) {
        return Configure.getAppProp("env").equals(stand.name().toLowerCase());
    }
}
