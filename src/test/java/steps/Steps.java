package steps;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.CacheService;

public abstract class Steps {
    public static final String dataFolder = Configure.getAppProp("data.folder");
    public static final String folder_logs = Configure.getAppProp("folder.logs");
    public static final String titleInformationSystem = Configure.getAppProp("title_information_system");
    protected JsonHelper jsonHelper = new JsonHelper();
    protected CacheService cacheService = new CacheService();
}
