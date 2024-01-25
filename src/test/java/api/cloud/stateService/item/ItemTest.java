package api.cloud.stateService.item;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.stateService.Item;
import models.cloud.stateService.extRelations.ExtRelation;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.stateService.ExtRelationsStep.createExtRelation;
import static steps.stateService.ExtRelationsStep.deleteExtRelation;
import static steps.stateService.StateServiceSteps.*;

@Tag("state_service")
@Epic("State Service")
@Feature("Item")
@DisabledIfEnv("prod")
public class ItemTest extends Tests {

    private static Project project;
    private static final String projects = "projects";
    private static final List<Integer> relationsIdsForDelete = new ArrayList<>();

    @BeforeAll
    public static void init() {
        project = Project.builder()
                .isForOrders(true)
                .build()
                .createObject();
    }

    @AfterAll
    public static void deleteTestData() {
        relationsIdsForDelete.forEach(x -> deleteExtRelation(projects, project.getId(), x));
    }

    @DisplayName("Получение списка primary items по items/as_secondary_items/")
    @TmsLink("1744953")
    @Test
    public void getPrimaryItemsListBySecondaryItemIdTest() {
        Item primaryItem = createItem(project);
        Item secondaryItem = createItem(project);
        Item primaryItem2 = createItem(project);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        ExtRelation extRelation2 = createExtRelation(projects, project.getId(), primaryItem2.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation2.getId());
        List<Item> primaryItemsList = getPrimaryItemsList(secondaryItem.getItemId());
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(primaryItem.getItemId())));
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(primaryItem2.getItemId())));
    }

    @DisplayName("Получение списка secondary items по items/as_primary_items/")
    @TmsLink("1745012")
    @Test
    public void getSecondaryItemsListByPrimaryItemIdTest() {
        Item primaryItem = createItem(project);
        Item secondaryItem = createItem(project);
        Item secondaryItem2 = createItem(project);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        ExtRelation extRelation2 = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem2.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation2.getId());
        List<Item> primaryItemsList = getSecondaryItemsList(primaryItem.getItemId());
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(secondaryItem.getItemId())));
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(secondaryItem2.getItemId())));
    }

    @DisplayName("Получение списка items у которых нет primary связи с конкретным item_id")
    @TmsLink("1745012")
    @Test
    public void getItemsListWithOutPrimaryRelationTest() {
        Item primaryItem = createItem(project);
        Item secondaryItem = createItem(project);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        List<Item> primaryItemsList = getItemsListWithOutPrimaryRelation(secondaryItem.getItemId());
    }

    @DisplayName("Получение списка items у которых нет secondary связи с конкретным item_id")
    @TmsLink("1745012")
    @Test
    public void getItemsListWithOutSecondaryRelationTest() {
        Item primaryItem = createItem(project);
        Item secondaryItem = createItem(project);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        List<Item> secondaryItemsList = getItemsListWithOutSecondaryRelation(primaryItem.getItemId());
    }

    @DisplayName("Получение списка items по фильтру include_deleted=true")
    @TmsLink("SOUL-8274")
    @Test
    public void getItemListWithStateNotEqualsDeleted() {
        List<Item> itemList = getItemsListByFilter("include_deleted=true");
        assertTrue(itemList.stream().anyMatch(x -> x.getData().get("state").equals("deleted")), "Item со state = deleted не найден");
    }

    @DisplayName("Перевод item в state = problem")
    @TmsLink("SOUL-8275")
    @Test
    public void changeItemStateToProblem() {
        String status = "problem";
        Item item = createItem(project);
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEventWithOneEvent.json")
                .set("$.order_id", item.getOrderId())
                .set("$.graph_id", item.getGraphId())
                .set("$.action_id", item.getActionId())
                .set("$.events[0].item_id", item.getItemId())
                .set("$.events[0].graph_id", item.getGraphId())
                .set("$.events[0].action_id", item.getActionId())
                .set("$.events[0].order_id", item.getOrderId())
                .set("$.events[0].status", status)
                .build();
        createBulkAddEvent(project.getId(), json2).assertStatus(201);
        Item itemById = getItemById(item.getItemId());
        assertEquals(status, itemById.getData().get("state"), "Items state не соответствует ожидаемому");
    }

    @DisplayName("Проверка что при запросе item по id возвращаются item со state = deleted")
    @TmsLink("SOUL-8694")
    @Test
    public void getItemByIdDeletedItemShouldBeReturnTest() {
        String status = "deleted";
        Item item = createItem(project);
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEventWithOneEvent.json")
                .set("$.order_id", item.getOrderId())
                .set("$.graph_id", item.getGraphId())
                .set("$.action_id", item.getActionId())
                .set("$.events[0].item_id", item.getItemId())
                .set("$.events[0].graph_id", item.getGraphId())
                .set("$.events[0].action_id", item.getActionId())
                .set("$.events[0].order_id", item.getOrderId())
                .set("$.events[0].status", status)
                .build();
        createBulkAddEvent(project.getId(), json2).assertStatus(201);
        Item itemById = getItemById(item.getItemId());
        assertEquals(status, itemById.getData().get("state"), "Items state не соответствует ожидаемому");
    }

    @DisplayName("Отправка на тарификацию все айтемы ордера")
    @TmsLink("SOUL-8891")
    @Test
    public void orderItemsPublicationTest() {
        Item item = createItem(project);
        uncheckedOrderItemsPublication(project.getId(), item.getOrderId())
                .assertStatus(200);
    }

    @DisplayName("Отправка на тарификацию все айтемы ордера")
    @TmsLink("SOUL-")
    @Test
    public void orderItemsPublicatiodnTest() {
        //todo завел задачу, доделать тест, когда реализуют валидацию на не валдиный ордер ид
        uncheckedOrderItemsPublication(project.getId(), "sdf")
                .assertStatus(401);
    }
}
