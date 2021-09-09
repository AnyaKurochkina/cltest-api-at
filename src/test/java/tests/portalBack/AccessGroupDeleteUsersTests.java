package tests.portalBack;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.portalBack.AccessGroupSteps;

import java.io.UnsupportedEncodingException;

@DisplayName("Набор тестов по удалению пользователя из группы доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.portalBack.AccessGroupDeleteUsersTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupDeleteUsersTests {
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();
    @ParameterizedTest
    @Order(1)
    @TmsLink("21")
    @DisplayName("Удаление пользователя из группы доступа")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Удаление пользователя")
    public void deleteUsers(String env, String tmsId) throws UnsupportedEncodingException {
        accessGroupSteps.deleteUsersFromGroup(env);
    }
}
