package models.productCatalog.forbiddenAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ForbiddenActionSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"jsonTemplate", "productName"}, callSuper = false)
@ToString(exclude = {"jsonTemplate", "productName"})
public class ForbiddenAction extends Entity {

    @JsonProperty("event_provider")
    private List<String> eventProvider;
    private String description;
    @JsonProperty("item_restriction")
    private Object itemRestriction;
    private String title;
    @JsonProperty("event_type")
    private List<String> eventType;
    @JsonProperty("config_restriction")
    private Object configRestriction;
    @JsonProperty("update_dt")
    private String updateDt;
    private String name;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("action")
    private String actionId;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("is_all_levels")
    private Boolean isAllLevels;
    @JsonProperty("direction")
    private String direction;
    private String jsonTemplate;
    private String productName;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/forbiddenAction/createForbiddenAction.json";
        productName = "/api/v1/forbidden_actions/";
        if (actionId == null) {
            Action action = Action.builder().actionName("action_for_forbidden_action_api_test").build().createObject();
            actionId = action.getActionId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.event_provider", eventProvider)
                .set("$.action", actionId)
                .set("$.item_restriction", itemRestriction)
                .set("$.event_type", eventType)
                .set("$.config_restriction", configRestriction)
                .set("$.is_all_levels", isAllLevels)
                .set("$.direction", direction)
                .build();
    }

    @Override
    protected void create() {
        if (isForbiddenActionExists(name)) {
            deleteForbiddenActionByName(name);
        }
        ForbiddenAction forbiddenAction = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(ForbiddenAction.class);
        id = forbiddenAction.getId();
        updateDt = forbiddenAction.getUpdateDt();
        createDt = forbiddenAction.getCreateDt();
        Assertions.assertNotNull(id, "Пример с именем: " + name + ", не создался");

    }

    @Override
    protected void delete() {
        deleteForbiddenActionById(id);
    }
}