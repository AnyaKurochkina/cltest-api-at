package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.StringUtils;
import core.helper.http.Http;
import lombok.*;
import models.Entity;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Optional;

import static tests.routes.TagServiceAPI.v1TagsCreate;
import static tests.routes.TagServiceAPI.v1TagsDelete;

@Builder @Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
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
    @JsonIgnore @EqualsAndHashCode.Exclude @ToString.Exclude
    Context context;
    String valueType;

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
        Tag tag = Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .api(v1TagsCreate, context.getType(), context.getId())
                .extractAs(Tag.class);
        StringUtils.copyAvailableFields(tag, this);
    }

    @Override
    protected void delete() {
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .api(v1TagsDelete, context.getType(), context.getId(), id);
    }
}
