package models.productCatalog;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.CompactTemplate;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.CreateItemVisualResponse;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.DefaultItem;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.FullTemplate;
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

import static core.helper.Configure.ProductCatalogURL;

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
    private final String productName = "/api/v1/item_visual_templates/";

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
                .set("$.compact_template", new JSONObject(compactTemplate))
                .set("$.full_template", new JSONObject(fullTemplate))
                .build();
    }

    @Override
    @Step("Создание шаблона визуализации")
    protected void create() {
        ProductCatalogSteps steps = new ProductCatalogSteps(productName, jsonTemplate);
        if (steps.isExists(name)) {
            String objectId = steps.getProductObjectIdByNameWithMultiSearch(name, GetVisualTemplateListResponse.class);
            steps.partialUpdateObject(objectId, new JSONObject().put("is_active", false));
            steps.deleteById(objectId);
        }
        itemId = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
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
        ProductCatalogSteps steps = new ProductCatalogSteps(productName, jsonTemplate);
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + itemId + "/")
                .assertStatus(204);
        Assertions.assertEquals(0, steps.getObjectListByName(name, GetVisualTemplateListResponse.class)
                .getItemsList().size(), "Шаблон визуализации с именем: " + name + ", не удалился");
    }
}
