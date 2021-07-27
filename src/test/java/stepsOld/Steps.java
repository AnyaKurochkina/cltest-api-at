package stepsOld;

import core.helper.Configurier;
import core.helper.JsonHelper;
import core.helper.Templates;
import core.vars.TestVars;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class Steps {
    protected final JsonHelper jsonHelper = new JsonHelper();
    protected final Templates templateSteps = new Templates();
    public static final String dataFolder = Configurier.getInstance().getAppProp("data.folder");
    protected TestVars testVars = new TestVars();
}

