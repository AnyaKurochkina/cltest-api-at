package models.cloud.productCatalog.visualTeamplate;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedHashMap;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.productCatalog.VisualTemplateSteps.*;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ItemVisualTemplate extends Entity implements IProductCatalog {

    @JsonProperty("event_type")
    private List<String> eventType;
    @JsonProperty("compact_template")
    private CompactTemplate compactTemplate;
    @JsonProperty("is_active")
    private Boolean isActive;
    @JsonProperty("full_template")
    private FullTemplate fullTemplate;
    @JsonProperty("event_provider")
    private List<String> eventProvider;
    private String name;
    @JsonProperty("default_item")
    private LinkedHashMap<String, Object> defaultItem;
    private String description;
    private String id;
    private String object_info;
    private String title;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("default_order")
    private Object defaultOrder;
    @JsonProperty("tag_list")
    private List<String> tagList;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/itemVisualTemplate/createItemVisual.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.is_active", isActive)
                .set("$.event_provider", eventProvider)
                .set("$.event_type", eventType)
                .set("$.compact_template", new JSONObject(compactTemplate))
                .set("$.full_template", new JSONObject(fullTemplate))
                .set("$.default_item", defaultItem)
                .set("$.default_order", defaultOrder)
                .set("$.tag_list", tagList)
                .set("$.object_info", object_info)
                .build();
    }

    @Override
    @Step("Создание шаблона визуализации")
    protected void create() {
        if (isVisualTemplateExists(name)) {
            String objectId = getVisualTemplateByName(name).getId();
            partialUpdateVisualTemplate(objectId, new JSONObject().put("is_active", false));
            deleteVisualTemplateById(objectId);
        }
        ItemVisualTemplate itemVisualTemplate = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post("/api/v1/item_visual_templates/")
                .assertStatus(201)
                .extractAs(ItemVisualTemplate.class);
        StringUtils.copyAvailableFields(itemVisualTemplate, this);
        Assertions.assertNotNull(id, "Шаблон визуализации с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление шаблона визуализации")
    protected void delete() {
        deleteVisualTemplateById(id).assertStatus(204);
        assertFalse(isVisualTemplateExists(name));
    }
}
