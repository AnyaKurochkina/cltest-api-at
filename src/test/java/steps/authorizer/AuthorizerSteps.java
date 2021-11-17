package steps.authorizer;

import core.CacheService;
import core.helper.Configure;
import core.helper.Http;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Folder;
import models.authorizer.InformationSystem;
import models.authorizer.Organization;
import steps.Steps;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.Objects;

@Log4j2
public class AuthorizerSteps extends Steps {
    public static final String URL = Configure.getAppProp("host_kong");

    @Step("Получить все проекты папки")
    public void getAllProjectFromFolder(String folderId) {
        ArrayList<String> projectId = new Http(URL)
                .get("authorizer/api/v1/folders/" + folderId + "/children")
                .assertStatus(200)
                .jsonPath()
                .get("data.name");
        System.out.println(projectId);
    }

    /**
     * @param title имя организации
     */
    @Step("Получение организации")
    public void getOrganizationByTitle(String title) {
//        String org = new Http(URL)
//                .get("authorizer/api/v1/organizations?page=1&per_page=25")
//                .assertStatus(200)
//                .jsonPath()
//                .getString(String.format("data.find{it.title=='%s'}", title));
//                Organization organization = Organization.builder()
//                .name(orgName)
//                .title(title)
//                .build();
//        //Сохранение организации
//        cacheService.saveEntity(organization);
    }

    @Step("Получение пути до папки/проекта")
    public String getPathToFolder(String target) {
        String url;
        if (target.startsWith("fold")) {
            url = "authorizer/api/v1/folders/" + target + "/path";
        } else if (target.startsWith("proj")) {
            url = "authorizer/api/v1/projects/" + target + "/path";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }

        String path = new Http(URL)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .get("data.path");
        Objects.requireNonNull(path);
        return path;
    }

    @Step("Получение родителя папки/проекта")
    public String getParentProject(String target) {
        String url;
        if (target.startsWith("fold")) {
            url = "authorizer/api/v1/folders/" + target + "/ancestors";
        } else if (target.startsWith("proj")) {
            url = "authorizer/api/v1/projects/" + target + "/ancestors";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }
        return Objects.requireNonNull(new Http(URL)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .get(String.format("data.find{it.name=='%s'}.parent", target)));
    }

}

