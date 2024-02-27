package api.cloud.portalBack;

import api.Tests;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.portalBack.AccessGroup;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.portalBack.AccessGroupSteps;
import steps.portalBack.PortalBackSteps;

@Epic("Управление")
@Feature("Группы доступа")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccessGroupTest extends Tests {
    String name = new Generex("[a-z]{5,15}").random();
    Project projectDev = Project.builder().isForOrders(true)
            .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV")).build().createObject();
    Project projectTest = Project.builder().isForOrders(true)
            .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("TEST")).build().createObject();

    @Test
    @Order(1)
    @TmsLink("377438")
    @DisplayName("Создание Группы доступа compute")
    void createAccessGroup() {
        AccessGroup.builder().name(name).projectName(projectDev.getId()).build().createObject();
    }

    @Test
    @Tag("health_check")
    @Order(2)
    @TmsLink("996161")
    @DisplayName("Создание Группы доступа vlt")
    void createAccessGroupVlt() {
        AccessGroup.builder().projectName(projectDev.getId()).codePurpose("vlt").build().createObjectPrivateAccess();
    }

    @Test
    @Tag("health_check")
    @Order(3)
    @TmsLink("648626")
    @DisplayName("Редактирование группы доступа")
    void editServiceAccount() {
        AccessGroup group = AccessGroup.builder().name(name).projectName(projectDev.getId()).name(name).build().createObject();
        group.editGroup("new description");
    }


    @DisabledIfEnv("dev")
    @Test
    @Order(4)
    @TmsLink("377442")
    @DisplayName("Добавление пользователя в группу доступа для среды TEST")
    void addUserAccessGroupTest() {
        AccessGroup accessGroup = AccessGroup.builder().name(new Generex("[a-z]{5,15}").random()).projectName(projectTest.getId()).build().createObject();
        AccessGroupSteps.addUsersToGroup(accessGroup, PortalBackSteps.getUsers(projectTest, "VTB4057583", accessGroup.getDomain()));
    }

    @Test
    @Tag("health_check")
    @Order(5)
    @TmsLink("377440")
    @DisplayName("Добавление пользователя в группу доступа для среды DEV")
    void addUserAccessGroupDev() {
        AccessGroup accessGroup = AccessGroup.builder().name(name).projectName(projectDev.getId()).build().createObject();
        AccessGroupSteps.addUsersToGroup(accessGroup, PortalBackSteps.getUsers(projectDev, "VTB4057583", accessGroup.getDomain()));
    }

    @Test
    @Tag("health_check")
    @Order(6)
    @TmsLink("377441")
    @DisplayName("Удаление пользователя из группы доступа")
    void deleteUserAccessGroup() {
        AccessGroup accessGroup = AccessGroup.builder().name(name).projectName(projectDev.getId()).build().createObject();
        String user = PortalBackSteps.getUsers(projectDev, "VTB4057583", accessGroup.getDomain());
        AccessGroupSteps.addUsersToGroup(accessGroup, user);
        AccessGroupSteps.removeUserFromGroup(accessGroup, user);
    }

    @Test
    @Tag("health_check")
    @Order(7)
    @TmsLink("377439")
    @MarkDelete
    @DisplayName("Удаление Группы доступа")
    void deleteAccessGroup() {
        AccessGroup.builder().name(name).projectName(projectDev.getId()).build().createObjectExclusiveAccess().deleteObject();
    }
}