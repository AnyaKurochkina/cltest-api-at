package steps.portalBack;

import core.helper.Configure;
import core.helper.Http;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.UnsupportedEncodingException;
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
        jsonHelper.getJsonTemplate("/accessGroup/users.json")
                .set("$.users", arr)
                .send(PortalBackURL)
                .post(String.format("projects/%s/access_groups/%s/group_users", group.getProjectName(), group.getName()))
                .assertStatus(201);
        group.addUser(username);
    }

    @SneakyThrows
    @Step("Добавление пользователя в группу доступа для проекта среды {env}")
    public void removeUserFromGroup(AccessGroup group, String user) {
        new Http(PortalBackURL)
                .delete(String.format("projects/%s/access_groups/%s/group_users?unique_name=%s", group.getProjectName(), group.getName(), URLEncoder.encode(user, String.valueOf(StandardCharsets.UTF_8))))
                .assertStatus(204);
        group.removeUser(user);
    }
}
