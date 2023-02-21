package api.cloud.stateService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.stateService.Item;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.stateService.StateServiceSteps.getItemIdOrderIdListByItemsIds;

@Tag("state_service")
@Epic("State Service")
@Feature("State Service api")
@DisabledIfEnv("prod")
public class StateServiceNegativeTest extends Tests {

    @Test
    @DisplayName("Получение списка связок order_id и item_id отфильтрованного по невалидным item_id")
    @TmsLink("1429736")
    public void getItemsIdOrderIdListFilteredByItemId() {
        List<Item> itemsIdOrderIdList = getItemIdOrderIdListByItemsIds("d08ae2aa-b1c6-11ed-afa1-0242ac120002").assertStatus(200)
                .jsonPath()
                .getList("list", Item.class);
        assertEquals(0, itemsIdOrderIdList.size());
    }
}
