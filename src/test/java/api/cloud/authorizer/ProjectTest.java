package api.cloud.authorizer;

import org.junit.MarkDelete;
import io.qameta.allure.*;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import api.Tests;

@Epic("Организационная структура")
@Feature("Проекты")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectTest extends Tests {

    @Order(1)
    @Test
    @TmsLink("377747")
    @DisplayName("Создание проекта")
    void createProject() {
        Project.builder().isForOrders(false).build().createObject();
    }

    @Order(2)
    @Test
    @TmsLink("720818")
    @DisplayName("Редактирование проекта")
    void editProject() {
        try(Project project = Project.builder().isForOrders(false).build().createObjectExclusiveAccess()) {
            project.setProjectName("newProjectName");
            project.edit();
        }
    }

    @Order(3)
    @Test
    @TmsLink("377748")
    @DisplayName("Удаление проекта")
    @MarkDelete
    void deleteProject() {
        Project.builder().isForOrders(false).build().createObject().deleteObject();
    }

}
