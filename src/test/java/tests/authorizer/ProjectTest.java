package tests.authorizer;

import org.junit.MarkDelete;
import io.qameta.allure.*;
import models.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

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
    @TmsLink("377748")
    @DisplayName("Удаление проекта")
    @MarkDelete
    void deleteProject() {
        Project.builder().isForOrders(false).build().createObject().deleteObject();
    }

}
