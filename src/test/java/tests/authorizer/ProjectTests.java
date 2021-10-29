package tests.authorizer;

import core.helper.Deleted;
import io.qameta.allure.*;
import models.authorizer.Project;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.authorizer.ProjectSteps;
import tests.Tests;

@Epic("Организационная структура")
@Feature("Проекты")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
@Execution(ExecutionMode.SAME_THREAD)
public class ProjectTests extends Tests {

    @Order(1)
    @Test
    @DisplayName("Создание проекта")
    void createProject() {
        Project.builder().isForOrders(false).build().createObject();
    }

    @Order(2)
    @Test
    @DisplayName("Удаление проекта")
    @Deleted
    void deleteProject() {
        Project.builder().isForOrders(false).build().createObject().deleteObject();
    }

}
