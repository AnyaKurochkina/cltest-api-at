package tests.roles;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import models.portalBack.AccessGroup;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import tests.Tests;

@Feature("Ролевая модель")
@Epic("Администратор заказов")
@Tags({@Tag("regress")})
@DisabledIfEnv("prod")
public class OrderAdminTest extends Tests {

    @Test
    @DisplayName("Роль \"Администратор заказов\". Недоступные действия.")
    void orderAdminUnavailableActions() {
        Project project = Project.builder().isForOrders(false)
                .projectEnvironmentPrefix(new ProjectEnvironmentPrefix("DEV"))
                .build()
                .createObject();
        AccessGroup accessGroup = AccessGroup.builder()
                .projectName(project.getId())
                .domain("corp.dev.vtb")
                .build()
                .createObjectPrivateAccess();

        Http.setFixedRole(Role.ORDER_SERVICE_ADMIN);
        AccessGroup.builder().projectName(project.getId()).build().negativeCreateRequest(403);
        accessGroup.negativeDeleteRequest(403);
        Assertions.assertEquals(Assertions.assertThrows(Http.StatusResponseException.class,
                () -> accessGroup.editGroup("new description")).getStatus() , 403);

    }
}
