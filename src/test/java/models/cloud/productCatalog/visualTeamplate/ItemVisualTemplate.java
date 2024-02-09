package models.cloud.productCatalog.visualTeamplate;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import static steps.productCatalog.ProductCatalogSteps.getProductCatalogAdmin;
import static steps.productCatalog.VisualTemplateSteps.deleteVisualTemplateById;
import static steps.productCatalog.VisualTemplateSteps.partialUpdateVisualTemplate;
import static tests.routes.ItemVisualTemplateProductCatalogApi.apiV1ItemVisualTemplatesCreate;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ItemVisualTemplate extends AbstractEntity implements IProductCatalog {

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

    @Step("Создание шаблона визуализации")
    public ItemVisualTemplate createObject() {
        return getProductCatalogAdmin()
                .body(this.toJson())
                .api(apiV1ItemVisualTemplatesCreate)
                .extractAs(ItemVisualTemplate.class)
                .deleteMode(Mode.AFTER_TEST);
    }

    @Override
    @Step("Удаление шаблона визуализации")
    public void delete() {
        partialUpdateVisualTemplate(id, new JSONObject().put("is_active", false));
        deleteVisualTemplateById(id);
    }
}
