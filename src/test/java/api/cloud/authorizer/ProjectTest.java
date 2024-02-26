package api.cloud.authorizer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;

@Epic("Организационная структура")
@Feature("Проекты")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
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
