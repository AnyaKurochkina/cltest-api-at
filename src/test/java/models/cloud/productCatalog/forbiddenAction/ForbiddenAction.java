package models.cloud.productCatalog.forbiddenAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.productCatalog.action.Action;
import models.cloud.feedService.action.EventTypeProvider;
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

    @JsonProperty("event_type_provider")
    private List<EventTypeProvider> eventTypeProvider;
    private String description;
    @JsonProperty("item_restriction")
    private Object itemRestriction;
    private String title;
    @JsonProperty("environment_type_restriction")
    private List<String> environmentTypeRestriction;
    @JsonProperty("context_restrictions")
    private Object contextRestrictions;
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
                .set("$.action", actionId)
                .set("$.item_restriction", itemRestriction)
                .set("$.event_type_provider", eventTypeProvider)
                .set("$.context_restriction", contextRestrictions)
                .set("$.is_all_levels", isAllLevels)
                .set("$.direction", direction)
                .build();
    }

    @Override
    @Step("Создание запрещенного действия")
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
        StringUtils.copyAvailableFields(forbiddenAction, this);
        Assertions.assertNotNull(id, "Запрещенное действие с именем: " + name + ", не создался");

    }

    @Override
    @Step("Удаление запрещенного действия")
    protected void delete() {
        deleteForbiddenActionById(id);
    }
}