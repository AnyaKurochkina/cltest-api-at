package steps;

import core.helper.Configure;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Steps {
    public static final String dataFolder = Configure.getAppProp("data.folder");
    public static final String folder_logs = Configure.getAppProp("folder.logs");
    public static final String dataJson = Configure.getAppProp("data.json");
    public static final String titleInformationSystem = Configure.getAppProp("title_information_system");
}
