package steps;

import core.helper.Configurier;
import core.helper.JsonHelper;
import core.CacheService;

public abstract class Steps {
    public static final String dataFolder = Configurier.getInstance().getAppProp("data.folder");
    protected final JsonHelper jsonHelper = new JsonHelper();
    protected final CacheService cacheService = new CacheService();
}
