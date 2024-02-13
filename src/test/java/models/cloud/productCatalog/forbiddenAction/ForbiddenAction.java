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
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ForbiddenActionSteps.*;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"updateDt"}, callSuper = false)
@ToString
public class ForbiddenAction extends Entity {

    @JsonProperty("event_type_provider")
    private List<EventTypeProvider> eventTypeProvider;
    private String description;
    @JsonProperty("item_restriction")
    private Object itemRestriction;
    private String title;
    @JsonProperty("context_restrictions")
    private Object contextRestrictions;
    @JsonProperty("update_dt")
    private String updateDt;
    private String object_info;
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

    @Override
    public Entity init() {
        if (actionId == null) {
            Action action = createAction(StringUtils.getRandomStringApi(7));
            actionId = action.getId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/forbiddenAction/createForbiddenAction.json")
                .set("$.title", title)
                .set("$.object_info", object_info)
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
        ForbiddenAction forbiddenAction = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post("/api/v1/forbidden_actions/")
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