package steps.portalBack;

import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.portalBack.AccessGroup;
import steps.Steps;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static core.helper.Configure.PortalBackURL;

@Log4j2
public class AccessGroupSteps extends Steps {

    /**
     * Метод добавляет пользователя в группу доступа
     * @param group      группа
     * @param username пользователь
     */
    @Step("Добавление пользователя в группу доступа для проекта среды {env}")
    public void addUsersToGroup(AccessGroup group, String username) {
        String[] arr = new String[]{username};
        JsonHelper.getJsonTemplate("/accessGroup/users.json")
                .set("$.users", arr)
                .send(PortalBackURL)
                .post("projects/{}/access_groups/{}/group_users", group.getProjectName(), group.getPrefixName())
                .assertStatus(201);
        group.addUser(username);
    }

    @SneakyThrows
    @Step("Добавление пользователя в группу доступа для проекта среды {env}")
    public void removeUserFromGroup(AccessGroup group, String user) {
        new Http(PortalBackURL)
                .delete("projects/{}/access_groups/{}/group_users?unique_name={}", group.getProjectName(), group.getPrefixName(), URLEncoder.encode(user, String.valueOf(StandardCharsets.UTF_8)))
                .assertStatus(204);
        group.removeUser(user);
    }
}
