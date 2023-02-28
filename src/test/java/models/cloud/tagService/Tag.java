package models.cloud.tagService;

import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.http.Http;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import models.Entity;

import java.util.Optional;

@SuperBuilder @Getter @ToString @AllArgsConstructor @NoArgsConstructor
public class Tag extends ContextEntity {
    String value;
    String key;
    String tagType;
    String id;
    String parent;
    String contextPath;

    @Override
    public Entity init() {
        super.init();
        key = Optional.ofNullable(key).orElse(new Generex("key_[a-z]{6}").random());
        value = Optional.ofNullable(value).orElse(new Generex("value_[a-z]{10}").random());
        tagType = Optional.ofNullable(tagType).orElse("user");
        return this;
    }

    @Override
    protected void create() {
        Tag tag = new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .post("/v1/{}/{}/tags/", contextType, contextId)
                .assertStatus(201)
                .extractAs(Tag.class);
        StringUtils.copyAvailableFields(tag, this);
    }

    @Override
    protected void delete() {
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .delete("/v1/{}/{}/tags/{}/", contextType, contextId, id)
                .assertStatus(204);
    }
}
