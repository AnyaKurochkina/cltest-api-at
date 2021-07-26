package tests.suites;

import core.helper.Configurier;
import core.helper.JsonHelper;
import core.vars.LocalThead;
import core.vars.TestVars;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import steps.AuthSteps;

public abstract class Steps {
    public static final String dataFolder = Configurier.getInstance().getAppProp("data.folder");
    protected final JsonHelper jsonHelper = new JsonHelper();
    protected final CacheService cacheService = new CacheService();
}
