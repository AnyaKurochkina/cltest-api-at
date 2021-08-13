package steps;

import core.helper.Configurier;
import core.helper.JsonHelper;
import core.CacheService;

public abstract class Steps {
    public static final String dataFolder = Configurier.getAppProp("data.folder");
    public static final String folder_logs = Configurier.getAppProp("folder.logs");
    public static final String titleInformationSystem = Configurier.getAppProp("title_information_system");
    protected JsonHelper jsonHelper = new JsonHelper();
    protected CacheService cacheService = new CacheService();
}
