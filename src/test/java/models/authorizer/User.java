package models.authorizer;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import lombok.*;
import models.Entity;
import org.json.JSONObject;

import java.util.*;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends Entity {
    private String firstname;
    private Date updatedAt;
    private List<MembersItem> members;
    private Boolean active;
    private Date createdAt;
    private String id;
    private String email;
    private String username;
    private String lastname;

    @Builder.Default
    transient Set<UserMember> bindings = new HashSet<>();
    transient String projectName;

    @Override
    public Entity init() {
        if (bindings.isEmpty())
            bindings.add(new UserMember("roles/admin", Collections.singleton("user:rvermakov@dev.vtb.ru")));
        if (projectName == null)
            projectName = ((Project) Project.builder().isForOrders(false).build().createObject()).getId();
        return this;
    }

    @SneakyThrows
    @Override
    public JSONObject toJson() {
        return new JSONObject("{\"policy\": {\"bindings\":" + JsonHelper.getCustomObjectMapper().writeValueAsString(bindings) + "}}");
    }

    @Override
    protected void create() {
        new Http(Configure.AuthorizerURL)
                .body(toJson())
                .post("projects/{}/policy/add-members", projectName)
                .assertStatus(201);
    }

    public static class UserBuilder {
        @SneakyThrows
        public UserBuilder addUser(String role, String user) {
            String prefix = "user:";
            if (bindings$value.stream().anyMatch(u -> u.role.equals(role))) {
                UserMember current = bindings$value.stream().filter(u -> u.role.equals(role)).findAny().orElseThrow(Exception::new);
                current.members.add(user);
                return this;
            }
            bindings$value.add(new UserMember(role, Collections.singleton(prefix + user)));
            return this;
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @AllArgsConstructor
    @Data
    public static class UserMember {
        @EqualsAndHashCode.Include
        String role;
        Set<String> members;
    }


    @Override
    protected void delete() {

    }


}