package tests.authorizer;

import org.junit.jupiter.api.Test;

import static steps.authorizer.AuthorizerSteps.*;

public class UserTest {
    @Test
    void name() {
        getUserList("proj-xazpppulba");
    }
}
