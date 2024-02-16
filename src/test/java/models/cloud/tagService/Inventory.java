package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import core.enums.Role;
import core.helper.StringUtils;
import core.helper.http.Http;
import lombok.*;
import models.Entity;
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v1.FilterResultV1Item;
import models.cloud.tagService.v2.FilterResultV2;
import models.cloud.tagService.v2.FilterResultV2Page;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static tests.routes.TagServiceAPI.*;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Inventory extends Entity {
    public static String DEFAULT_TYPE = "test_type";
    @EqualsAndHashCode.Include
    @ToString.Include
    String id;
    String objectType;
    String contextPath;
    @JsonIgnore
    Context context;
    List<String> dataSources;
    List<String> securityPrincipals;
    List<String> managers;

    LocalDateTime createdAt, updatedAt;

    @Override
    public Entity init() {
        Objects.requireNonNull(context, "Не задан контекст");
        if (Objects.isNull(contextPath))
            contextPath = context.getContextPath();
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
        Inventory inventory = Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .api(v2InventoriesCreate, context.getType(), context.getId())
                .extractAs(Inventory.class);
        StringUtils.copyAvailableFields(inventory, this);
    }

    public FilterResultV2 inventoryListItemV2(FilterResultV2Page filterResult) {
        return filterResult.getList().stream().filter(i -> i.getInventory().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Не найден item с id " + id));
    }

    public FilterResultV1Item inventoryListItemV1(FilterResultV1 filterResult) {
        return filterResult.getList().stream().filter(i -> i.getInventory().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Не найден item с id " + id));
    }

    @Override
    public void delete() {
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .api(v2InventoriesDelete, context.getType(), context.getId(), id);
    }

    public void updateAcl(List<String> securityPrincipals, List<String> managers) {
        JSONObject req = new JSONObject()
                .put("security_principals", securityPrincipals)
                .put("managers", managers);
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(req)
                .api(v1InventoryAclUpdate, id);
    }
}
