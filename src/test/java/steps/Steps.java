package steps;

import core.helper.Configurier;
import core.helper.JsonHelper;
import core.CacheService;

public abstract class Steps {
    public static final String dataFolder = Configurier.getInstance().getAppProp("data.folder");
    public static final String folder_logs = Configurier.getInstance().getAppProp("folder.logs");
    public static final String titleInformationSystem = Configurier.getInstance().getAppProp("title_information_system");
    protected JsonHelper jsonHelper = new JsonHelper();
    protected CacheService cacheService = new CacheService();
}
