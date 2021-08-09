package tests;

import core.vars.LocalThead;
import core.vars.TestVars;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public interface Tests {
    TestVars testVars = new TestVars();
    @BeforeEach
    default void beforeScenarios()  {
        LocalThead.setTestVars(testVars);
    }

    @AfterEach
    default void afterScenarios(){
        LocalThead.setTestVars(null);
    }

}
