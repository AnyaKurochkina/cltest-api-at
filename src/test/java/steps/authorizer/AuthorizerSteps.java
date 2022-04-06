package steps.authorizer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.authorizer.User;
import models.authorizer.UserItem;
import models.authorizer.UserList;
import models.tarifficator.TariffPlan;
import steps.Steps;
import steps.tarifficator.TariffPlanSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.AuthorizerURL;
import static core.helper.Configure.PortalBackURL;

@Log4j2
public class AuthorizerSteps extends Steps {

    @Step("Получить все проекты папки")
    public static void getAllProjectFromFolder(String folderId) {
        ArrayList<String> projectId = new Http(AuthorizerURL)
                .get("folders/" + Objects.requireNonNull(folderId) + "/children")
                .assertStatus(200)
                .jsonPath()
                .get("data.name");
        log.info(projectId);
    }

    @Step("Получение пути до папки/проекта")
    public static String getPathToFolder(String target) {
        String url;
        if (Objects.requireNonNull(target).startsWith("fold")) {
            url = "folders/" + target + "/path";
        } else if (target.startsWith("proj")) {
            url = "projects/" + target + "/path";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }

        String path = new Http(AuthorizerURL)
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
            url = "folders/" + target + "/ancestors";
        } else if (target.startsWith("proj")) {
            url = "projects/" + target + "/ancestors";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }
        return Objects.requireNonNull(new Http(AuthorizerURL)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .get(String.format("data.find{it.name=='%s'}.parent", target)));
    }

    @Step("Получение пользователя из LDAP")
    public static String getLDAPUserList(Project project, String username) {
        return new Http(PortalBackURL)
                .get("users?q={}&project_name={}", username, project.getId())
                .assertStatus(200)
                .jsonPath()
                .get("[0].unique_name");
    }

    @SneakyThrows
    @Step("Получение списка пользователей")
    public static List<UserItem> getUserList(String projectId) {
        @SuppressWarnings (value="unchecked")
        List<UserItem> users = (List<UserItem>) listEntities(AuthorizerURL + "projects/" + projectId + "/users?", UserItem.class);
        return users;
    }

}

