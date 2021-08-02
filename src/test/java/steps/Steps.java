package steps;

import core.helper.Configurier;
import core.helper.JsonHelper;
import core.CacheService;

public abstract class Steps {
    public static final String dataFolder = Configurier.getInstance().getAppProp("data.folder");
    public static final String folder_logs = Configurier.getInstance().getAppProp("folder.logs");
    public static final String titleInformationSystem = Configurier.getInstance().getAppProp("title_information_system");
    protected final JsonHelper jsonHelper = new JsonHelper();
    protected final CacheService cacheService = new CacheService();
}
