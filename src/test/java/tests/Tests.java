package tests;

import core.vars.LocalThead;
import core.vars.TestVars;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class Tests {
    protected TestVars testVars = new TestVars();
    @BeforeEach
    public void beforeScenarios()  {
        LocalThead.setTestVars(testVars);
    }

    @AfterEach
    public void afterScenarios(){
        LocalThead.setTestVars(null);
    }
}
