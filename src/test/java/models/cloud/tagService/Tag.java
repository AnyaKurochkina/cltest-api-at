package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.http.Http;
import lombok.*;
import models.Entity;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Optional;

@Builder @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag extends Entity {
    String value;
    String key;
    String tagType;
    String id;
    String parent;
    String contextPath;
    @JsonIgnore
    Context context;

    @Override
    public Entity init() {
        Objects.requireNonNull(context, "Не задан контекст");
        key = Optional.ofNullable(key).orElse(new Generex("key_[a-z]{6}").random());
        value = Optional.ofNullable(value).orElse(new Generex("value_[a-z]{10}").random());
        tagType = Optional.ofNullable(tagType).orElse("user");
        return this;
    }

    @Override
    public JSONObject toJson() {
        return serialize();
    }

    @Override
    protected void create() {
        Tag tag = new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .post("/v1/{}/{}/tags/", context.getType(), context.getId())
                .assertStatus(201)
                .extractAs(Tag.class);
        StringUtils.copyAvailableFields(tag, this);
    }

    @Override
    protected void delete() {
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .delete("/v1/{}/{}/tags/{}/", context.getType(), context.getId(), id)
                .assertStatus(204);
    }
}
