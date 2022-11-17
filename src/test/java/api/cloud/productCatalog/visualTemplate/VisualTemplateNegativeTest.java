package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.visualTeamplate.CompactTemplate;
import models.cloud.productCatalog.visualTeamplate.FullTemplate;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json");
    CompactTemplate compactTemplate = CompactTemplate.builder().name(("name"))
            .type(("type")).status(("status")).build();
    FullTemplate fullTemplate = FullTemplate.builder().type("type").value(Arrays.asList("value", "value2")).build();

    @DisplayName("Негативный тест на создание шаблона визуализации с неуникальной связкой EventType-EventProvider")
    @TmsLink("682836")
    @Test
    public void createVisualTemplateWithNotUniqueEventTypeEventProvider() {
        String name = "create_visual_template_with_not_unique_type_provider";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        JSONObject jsonObject = JsonHelper.getJsonTemplate("productCatalog/itemVisualTemplate/createItemVisual.json")
                .set("name", "visual")
                .set("event_provider", Collections.singletonList("docker"))
                .set("event_type", Collections.singletonList("app"))
                .set("is_active", true).build();
        Response response = steps.createProductObject(jsonObject).assertStatus(422);
        steps.partialUpdateObject(visualTemplates.getId(), new JSONObject().put("is_active", false));
        assertEquals(name, response.jsonPath().get("name[0]").toString());
        assertEquals(visualTemplates.getId(), response.jsonPath().get("id[0]").toString());
    }

    @DisplayName("Негативный тест на получение шаблона визуализации по Id без токена")
    @TmsLink("643649")
    @Test
    public void getVisualTemplateByIdWithOutToken() {
        String name = "get_by_id_with_out_token_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        steps.getByIdWithOutToken(visualTemplates.getId());
    }

    @DisplayName("Негативный тест на создание шаблона отображения с неуникальным именем")
    @TmsLink("682891")
    @Test
    public void createVisualTemplateWithNonUniqueName() {
        String name = "create_with_same_name_item_visual_template_test_api";
        ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(name)).assertStatus(400);
    }

    @DisplayName("Негативный тест на создание шаблона визуализации с недопустимыми символами в имени")
    @TmsLink("643672")
    @Test
    public void createVisualTemplateWithInvalidCharacters() {
        assertAll("Шаблона визуализации создался с недопустимым именем",
                () -> steps.createProductObject(steps.createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> steps.createProductObject(steps.createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> steps.createProductObject(steps.createJsonObject("имя")).assertStatus(500),
                () -> steps.createProductObject(steps.createJsonObject("Имя")).assertStatus(500),
                () -> steps.createProductObject(steps.createJsonObject("a&b&c")).assertStatus(500),
                () -> steps.createProductObject(steps.createJsonObject("")).assertStatus(400),
                () -> steps.createProductObject(steps.createJsonObject(" ")).assertStatus(400)
        );
    }

    @DisplayName("Негативный тест на создание шаблона отображения без обязательного параметра status и статусом is_active true")
    @TmsLink("742493")
    @Test
    public void createInvalidVisualTemplate() {
        steps.createProductObject(JsonHelper.getJsonFromFile("productCatalog/itemVisualTemplate/InvalidItemVisual.json"))
                .assertStatus(500);
    }
}
