package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.itemVisualItem.CompactTemplate;
import httpModels.productCatalog.itemVisualItem.CreateItemVisualResponse;
import httpModels.productCatalog.itemVisualItem.DefaultItem;
import httpModels.productCatalog.itemVisualItem.FullTemplate;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.GetVisualTemplateListResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

@Log4j2
@Builder
@Getter
public class ItemVisualTemplates extends Entity {

    private List<String> eventType;
    private CompactTemplate compactTemplate;
    private Boolean isActive;
    private FullTemplate fullTemplate;
    private List<String> eventProvider;
    private String name;
    private DefaultItem defaultItem;
    private String description;
    private String itemId;
    private String title;
    private String jsonTemplate;
    private final String productName = "item_visual_templates/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/itemVisualTemplate/createItemVisual.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.is_active", isActive)
                .set("$.event_provider", eventProvider)
                .set("$.event_type", eventType)
                .build();
    }

    @Override
    @Step("Создание шаблона визуализации")
    protected void create() {
        itemId = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateItemVisualResponse.class)
                .getId();
        Assertions.assertNotNull(itemId, "Шаблон визуализации с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление шаблона визуализации")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + itemId + "/")
                .assertStatus(204);
        ProductCatalogSteps steps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertEquals(0, steps.getObjectByName(name, GetVisualTemplateListResponse.class)
                .getItemsList().size(), "Шаблон визуализации с именем: " + name + ", не удалился");
    }
}
