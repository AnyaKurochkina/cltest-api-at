package tests.portalBack;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import steps.portalBack.AccessGroupSteps;
import steps.portalBack.PortalBackSteps;
import tests.Tests;

@Epic("Управление")
@Feature("Группы доступа")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class AccessGroupTest extends Tests {

    @Test
    @Order(1)
    @DisplayName("Создание Группы доступа")
    void createAccessGroup() {
        AccessGroup.builder().name("accessgroup").build().createObject();
    }

    @Test
    @Order(2)
    @DisplayName("Добавление пользователя в группу доступа")
    void addUserAccessGroup() {
        AccessGroupSteps accessGroupSteps = new AccessGroupSteps();
        PortalBackSteps portalBackSteps = new PortalBackSteps();
        AccessGroup accessGroup = AccessGroup.builder().description("accessgroup").build().createObject();
        Project project = Project.builder().id(accessGroup.getProjectName()).build().createObject();
        accessGroupSteps.addUsersToGroup(accessGroup, portalBackSteps.getUsers(project, "VTB4043473"));
    }

    @Test
    @Order(3)
    @DisplayName("Удаление пользователя из группы доступа")
    void deleteUserAccessGroup() {
        AccessGroupSteps accessGroupSteps = new AccessGroupSteps();
        PortalBackSteps portalBackSteps = new PortalBackSteps();
        AccessGroup accessGroup = AccessGroup.builder().description("accessgroup").build().createObject();
        Project project = Project.builder().id(accessGroup.getProjectName()).build().createObject();
        String user = portalBackSteps.getUsers(project, "VTB4043473");
        accessGroupSteps.addUsersToGroup(accessGroup, user);
        accessGroupSteps.removeUserFromGroup(accessGroup, user);
    }

    @Test
    @Order(4)
    @MarkDelete
    @DisplayName("Удаление Группы доступа")
    void deleteAccessGroup() {
        AccessGroup.builder().description("accessgroup").build().createObject().deleteObject();
    }
}