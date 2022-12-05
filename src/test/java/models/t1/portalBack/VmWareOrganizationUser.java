package models.t1.portalBack;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class VmWareOrganizationUser extends Entity {

    @JsonProperty("is_enabled")
    private boolean isEnabled;
    private String password;
    @JsonProperty("email_address")
    private String emailAddress;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("role_id")
    private String roleId;
    private String telephone;
    private String username;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/t1/createUser.json")
                .set("$.telephone", telephone)
                .set("$.username", username)
                .set("$.role_id", roleId)
                .set("$.full_name", fullName)
                .set("$.email_address", emailAddress)
                .set("$.password", password)
                .set("$.is_enabled", isEnabled)
                .build();
    }

    @Override
    protected void create() {

    }

    @Override
    protected void delete() {

    }
}