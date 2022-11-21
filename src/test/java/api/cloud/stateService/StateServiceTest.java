package api.cloud.stateService;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import static org.junit.jupiter.api.Assertions.*;
import static steps.stateService.StateServiceSteps.*;

@Tag("state_service")
@Epic("State Service")
@Feature("State Service api")
@DisabledIfEnv("prod")
public class StateServiceTest extends Tests {

    @Test
    @DisplayName("Получение данных версии State Service")
    @TmsLink("1080847")
    public void getStateServiceVersionTest() {
        Response resp = getStateServiceVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
        assertNotNull(resp.jsonPath().get("stage"));
    }

    @Test
    @DisplayName("Передача в поле status пустой строки в Events")
    @TmsLink("1129758")
    public void createEventsWithStatusEmptyString() {
        Response response = createEventStateService(new JSONObject("{\n" +
                "            \"created_row_dt\": \"2022-08-25T11:37:50.346352+03:00\",\n" +
                "            \"create_dt\": \"2022-08-25T11:37:50.346362+03:00\",\n" +
                "            \"order_id\": \"7e2f2f29-2071-448c-86d8-ebe0dae77ddd\",\n" +
                "            \"action_id\": \"5037e95b-9100-4e94-897a-3c235f791f97\",\n" +
                "            \"graph_id\": \"7e2f2f29-2071-448c-86d8-ebe0dae77ddd\",\n" +
                "            \"type\": \"vm\",\n" +
                "            \"subtype\": \"parent\",\n" +
                "            \"status\": \"\",\n" +
                "            \"data\": null,\n" +
                "            \"item_id\": \"379d1263-e3d3-4fd9-92c9-d955922779ea\",\n" +
                "            \"update_data\": null\n" +
                "        }"));
        assertTrue(response.jsonPath().getString("status").isEmpty(), "Поле status не пустое");
    }

    @DisplayName("Получение статуса health")
    @TmsLink("1139586")
    @Test
    public void healthTest() {
        assertEquals("ok", getHealthStateService());
    }

    @Test
    @DisplayName("Получение item с параметром with_folder=true")
    @TmsLink("1283840")
    public void getItemWithFolderTrueTest() {
        String id = getItemsList().get(0).getItemId();
        assertFalse(getItemByIdAndFilter(id, "with_folder=true").getFolder().isEmpty(), "Значение поля folder пустое");
    }
}
