package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import core.enums.Role;
import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.http.Http;
import lombok.*;
import models.Entity;
import models.cloud.tagService.v1.FilterResultItemV1;
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v2.FilterResultV2;
import models.cloud.tagService.v2.FilterResultItemV2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.json.JSONObject;
import steps.resourceManager.ResourceManagerSteps;

import java.util.*;

@Builder @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Inventory extends Entity {
    public static String DEFAULT_TYPE = "test_type";
    @EqualsAndHashCode.Include @ToString.Include
    String id;
    String objectType;
    String contextPath;
    @JsonIgnore
    Context context;

    @Override
    public Entity init() {
        Objects.requireNonNull(context, "Не задан контекст");
        contextPath = Optional.ofNullable(contextPath).orElse(ResourceManagerSteps.getProjectPath(context.getType(), context.getId()) + "/");
        id = Optional.ofNullable(id).orElse(UUID.randomUUID().toString());
        objectType = Optional.ofNullable(objectType).orElse(DEFAULT_TYPE);
        return this;
    }

    @Override
    public JSONObject toJson() {
        return serialize();
    }

    @Override
    protected void create() {
        Inventory inventory = new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .post("/v2/{}/{}/inventories/", context.getType(), context.getId())
                .assertStatus(201)
                .extractAs(Inventory.class);
        StringUtils.copyAvailableFields(inventory, this);
    }

    public FilterResultItemV2 inventoryListItemV2(FilterResultV2 filterResult) {
        return filterResult.getList().stream().filter(i -> i.getInventory().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Не найден item с id " + id));
    }

    public FilterResultItemV1 inventoryListItemV1(FilterResultV1 filterResult) {
        return filterResult.getList().stream().filter(i -> i.getInventory().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Не найден item с id " + id));
    }

    @Override
    protected void delete() {
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .delete("/v2/{}/{}/inventories/{}/", context.getType(), context.getId(), id)
                .assertStatus(204);
    }

    public void updateAcl(List<String> securityPrincipals, List<String> managers) {
        JSONObject req = new JSONObject()
                .put("security_principals", securityPrincipals)
                .put("managers", managers);
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(req)
                .put("/v1/inventory-acl/{}/", id)
                .assertStatus(200);
    }

}
