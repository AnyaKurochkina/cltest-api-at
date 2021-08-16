package tests.portalBack;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.portalBack.AccessGroupSteps;
import steps.portalBack.PortalBackSteps;

@DisplayName("Набор тестов по добавлению пользователя в группы доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.AccessGroupAddUsersTests")
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupAddUsersTests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();
    PortalBackSteps portalBackSteps = new PortalBackSteps();
    @ParameterizedTest
    @Order(1)
    @DisplayName("Добавление пользователя в группу доступа")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Добавление пользователя")
    public void addUsers(String env) {
        accessGroupSteps.addUsersToGroup(env, portalBackSteps.getUsers(env, "VTB4043473"));
    }
}
