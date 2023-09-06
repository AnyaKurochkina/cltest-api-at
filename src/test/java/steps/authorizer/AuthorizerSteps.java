package steps.authorizer;

import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.UserItem;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;
import java.util.Objects;

import static core.helper.Configure.IamURL;
import static core.helper.Configure.ResourceManagerURL;

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

    @Step("Получение имени контекста")
    public static String getContextName(String target) {
        String url;
        if (Objects.requireNonNull(target).startsWith("fold")) {
            url = "/v1/folders/" + target;
        } else if (target.startsWith("proj")) {
            url = "/v1/projects/" + target;
        } else {
            url = "/v1/organizations/" + target;
        }
        return Objects.requireNonNull(new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .getString("data.title"));
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

    @Step("Удаление папки")
    public static void deleteFolderByNameT1(String name) {
        new Http(Configure.ResourceManagerURL)
                .setRole(Role.SUPERADMIN)
                .delete("/v1/folders/" + name)
                .assertStatus(204);
    }

    @Step("Создание папки")
    public static String createFolder(String title) {
        Organization org = Organization.builder().build().createObject();
        JSONObject jsonObject = Folder.builder()
                .title(title)
                .build()
                .toJson();
        String url = String.format("/v1/organizations/%s/folders", org.getName());
        return new Http(Configure.ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(jsonObject)
                .post(url)
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }
}

