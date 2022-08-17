package steps.authorizer;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Organization;
import models.authorizer.Project;
import models.authorizer.UserItem;
import steps.Steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.*;

@Log4j2
public class AuthorizerSteps extends Steps {

    @Step("Получить все проекты папки")
    public static void getAllProjectFromFolder(String folderId) {
        ArrayList<String> projectId = new Http(IamURL)
                .get("/v1/folders/" + Objects.requireNonNull(folderId) + "/children")
                .assertStatus(200)
                .jsonPath()
                .get("data.name");
        log.info(projectId);
    }

    @Step("Получение пути до папки/проекта")
    public static String getPathToFolder(String target) {
        String url;
        if (Objects.requireNonNull(target).startsWith("fold")) {
            url = "/v1/folders/" + target + "/path";
        } else if (target.startsWith("proj")) {
            url = "/v1/projects/" + target + "/path";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }

        String path = new Http(IamURL)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .get("data.path");
        Objects.requireNonNull(path);
        return path;
    }

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

    @Step("Получение пользователя из LDAP")
    public static String getLDAPUserList(Project project, String username) {
        return new Http(PortalBackURL)
                .get("/v1/users?q={}&project_name={}", username, project.getId())
                .assertStatus(200)
                .jsonPath()
                .get("[0].unique_name");
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

