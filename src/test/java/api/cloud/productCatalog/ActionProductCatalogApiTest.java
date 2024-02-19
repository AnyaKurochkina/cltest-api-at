package api.cloud.productCatalog;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.stateService.Item;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductCatalogSteps.*;
import static steps.stateService.StateServiceSteps.createItem;

@Epic("Product Catalog")
@Feature("Product Catalog API")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class ActionProductCatalogApiTest extends Tests {

    @DisplayName("Получение версии сервиса продуктовый каталог")
    @TmsLink("1080851")
    @Test
    public void getProductCatalogVersionTest() {
        Response resp = getProductCatalogVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
        assertNotNull(resp.jsonPath().get("stage"));
    }

    @DisplayName("Получение статуса health")
    @TmsLink("1139584")
    @Test
    public void healthTest() {
        assertEquals("ok", getHealthStatusProductCatalog());
    }

    @DisplayName("Проверка item restriction")
    @TmsLink("SOUL-9055")
    @Test
    public void checkItemRestriction() {
        Project project = Project.builder().build().onlyGetObject();
        Item item = createItem(project);
        JSONObject json = new JSONObject().put("data_item", item.getData()).put("item_restriction", "state == 'on'");
        Response response = checkItemRestrictions(json).assertStatus(200);
        assertTrue(response.extractAs(Boolean.class));
        JSONObject json2 = new JSONObject().put("data_item", item.getData()).put("item_restriction", "state == 'off'");
        Response response2 = checkItemRestrictions(json2).assertStatus(200);
        assertFalse(response2.extractAs(Boolean.class));
    }

    @DisplayName("Проверка не существующего в item restriction параметра")
    @TmsLink("SOUL-")
    @Disabled
    @Test
    public void checkNotExistParamInItemRestriction() {
        //todo завести задачу. доделать, когда реализуют функционал
        Project project = Project.builder().build().onlyGetObject();
        Item item = createItem(project);
        JSONObject json = new JSONObject().put("data_item", item.getData()).put("item_restriction", "notexist == 'on'");
        Response response = checkItemRestrictions(json).assertStatus(500);
    }
}
