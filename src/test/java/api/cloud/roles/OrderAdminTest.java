package api.cloud.roles;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.StatusResponseException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.portalBack.AccessGroup;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import api.Tests;

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
        Assertions.assertEquals(Assertions.assertThrows(StatusResponseException.class,
                () -> accessGroup.editGroup("new description")).getStatus() , 403);

    }
}
