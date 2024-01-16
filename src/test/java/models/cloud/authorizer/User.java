package models.cloud.authorizer;

import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import lombok.*;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.authorizer.AuthorizerSteps;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends Entity {
    @Builder.Default
    Set<UserMember> bindings = new HashSet<>();
    String projectName;

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
        new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(toJson())
                .post("/v1/projects/{}/policy/add-members", projectName)
                .assertStatus(201);
        List<UserItem> users = AuthorizerSteps.getUserList(projectName);
        Assertions.assertTrue(bindings.stream().anyMatch(b -> b.getMembers().stream().anyMatch(m -> users.stream().anyMatch(u -> m.endsWith(u.getEmail())))),
                "Пользователь не создан");
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
        new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(toJson())
                .post("/v1/projects/{}/policy/remove-members", projectName)
                .assertStatus(201);
        List<UserItem> users = AuthorizerSteps.getUserList(projectName);
        Assertions.assertFalse(bindings.stream().anyMatch(b -> b.getMembers().stream().anyMatch(m -> users.stream().anyMatch(u -> m.endsWith(u.getEmail())))),
                "Пользователь не удален");
    }


}