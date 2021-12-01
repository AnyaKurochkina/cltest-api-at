package tests.productCatalog;

import core.helper.Deleted;
import httpModels.productCatalog.Action.patchAction.response.PatchActionResponse;
import models.productCatalog.Action;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.productCatalog.ActionsSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
public class ActionsTest extends Tests {
    ActionsSteps actionsSteps = new ActionsSteps();
    Action action;

    @Order(1)
    @DisplayName("Создание экшена в продуктовом каталоге")
    @Test
    public void createAction() {
        action = Action.builder().actionName("TestObjectAT").build().createObject();
    }

    @Order(2)
    @DisplayName("Поиск экшена по имени, с использованием multiSearch")
    @Test
    public void searchActionByName() {
        String actionIdWithMultiSearch = actionsSteps.getActionByNameWithMultiSearch("TestObjectAT");
        assertAll("I Love messages ♡",
                () -> assertNotNull(actionIdWithMultiSearch, String.format("Экшен с именем: %s не найден", "TestObjectAT")),
                () -> assertEquals(action.getActionId(), actionIdWithMultiSearch));
    }

    @Order(3)
    @DisplayName("Негативный тест на создание экшена с двумя параметрами одновременно graph_version_pattern и graph_version")
    @Test
    public void doubleVersionTest() {
        try {
            actionsSteps.createAction(Action.builder().actionName("NegativeAction").build().init().getTemplate()
                    .set("$.version", "1.1.1")
                    .set("$.graph_version", "1.0.0")
                    .set("$.graph_version_pattern", "1.")
                    .build()).assertStatus(404);
        } finally {
            String actionIdForDelete = actionsSteps.getActionByNameWithMultiSearch("NegativeAction");
            actionsSteps.deleteAction(actionIdForDelete);
        }
    }

    @Order(4)
    @DisplayName("Обновление экшена без указания версии, вресия должна инкрементироваться")
    @Test
    public void patchTest() {
        PatchActionResponse patchActionResponse = actionsSteps.patchAction("TestObjectAT", action.getGraphId(), action.getActionId());
        Assertions.assertEquals("1.1.2", patchActionResponse.getLastVersion());
    }

    @Order(5)
    @DisplayName("Негативный тест на обновление экшена до той же версии/текущей")
    @Test
    public void sameVersionTest() {
        actionsSteps.patchActionRow(Action.builder().actionName("TestObjectAT").build().init().getTemplate()
                .set("$.version", "1.1.2")
                .build(), action.getActionId()).assertStatus(404);
    }

    @Order(100)
    @Test
    @DisplayName("Удаление экшена")
    @Deleted
    public void deleteAction() {
        try (Action action = Action.builder().actionName("TestObjectAT").build().createObjectExclusiveAccess()) {
            action.deleteObject();
        }
    }
}

