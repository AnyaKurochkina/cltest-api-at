package tests.portalBack;

import core.helper.Deleted;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import steps.portalBack.AccessGroupSteps;
import steps.portalBack.PortalBackSteps;
import tests.Tests;

@Epic("Управление")
@Feature("Группы доступа")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class AccessGroupTests extends Tests {

    @Test
    @Order(1)
    @DisplayName("Создание Группы доступа")
    void createAccessGroup() {
        AccessGroup.builder().isForOrders(false).build().createObject();
    }

    @Test
    @Order(2)
    @DisplayName("Добавление пользователя в группу доступа")
    void addUserAccessGroup() {
        AccessGroupSteps accessGroupSteps = new AccessGroupSteps();
        PortalBackSteps portalBackSteps = new PortalBackSteps();
        AccessGroup accessGroup = AccessGroup.builder().isForOrders(false).build().createObject();
        Project project = Project.builder().id(accessGroup.getProjectName()).build().createObject();
        accessGroupSteps.addUsersToGroup(accessGroup, portalBackSteps.getUsers(project, "VTB4043473"));
    }

    @Test
    @Order(3)
    @DisplayName("Удаление пользователя из группы доступа")
    void deleteUserAccessGroup() {
        AccessGroupSteps accessGroupSteps = new AccessGroupSteps();
        PortalBackSteps portalBackSteps = new PortalBackSteps();
        AccessGroup accessGroup = AccessGroup.builder().isForOrders(false).build().createObject();
        Project project = Project.builder().id(accessGroup.getProjectName()).build().createObject();
        String user = portalBackSteps.getUsers(project, "VTB4043473");
        accessGroupSteps.addUsersToGroup(accessGroup, user);
        accessGroupSteps.removeUserFromGroup(accessGroup, user);
    }

    @Test
    @Order(4)
    @Deleted
    @DisplayName("Удаление Группы доступа")
    void deleteAccessGroup() {
        AccessGroup.builder().isForOrders(false).build().createObject().deleteObject();
    }
}