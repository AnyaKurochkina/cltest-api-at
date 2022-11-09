package steps.authorizer;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.UserItem;
import steps.Steps;

import java.util.List;
import java.util.Objects;

import static core.helper.Configure.*;

@Log4j2
public class AuthorizerSteps extends Steps {

    @Step("Получение родителя папки/проекта")
    public static String getParentProject(String target) {
        String url;
        if (Objects.requireNonNull(target).startsWith("fold")) {
            url = "/v1/folders/" + target + "/ancestors";
        } else if (target.startsWith("proj")) {
            url = "/v1/projects/" + target + "/ancestors";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }
        return Objects.requireNonNull(new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .get(String.format("data.find{it.name=='%s'}.parent", target)));
    }

    @Step("Получение списка пользователей организации по тексту '{text}'")
    public static List<UserItem> findUsers(String text, Organization org) {
        @SuppressWarnings(value = "unchecked")
        List<UserItem> users = (List<UserItem>) listEntities(IamURL, String.format("/v1/organizations/%s/users/search?search=%s", org.getName(), text), UserItem.class, Role.CLOUD_ADMIN);
        return users;
    }

    @SneakyThrows
    @Step("Получение списка пользователей")
    public static List<UserItem> getUserList(String projectId) {
        @SuppressWarnings(value = "unchecked")
        List<UserItem> users = (List<UserItem>) listEntities(IamURL, "/v1/projects/" + projectId + "/users?", UserItem.class, Role.CLOUD_ADMIN);
        return users;
    }

}

