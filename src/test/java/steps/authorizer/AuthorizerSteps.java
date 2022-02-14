package steps.authorizer;

import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import steps.Steps;

import java.util.ArrayList;
import java.util.Objects;

import static core.helper.Configure.AuthorizerURL;

@Log4j2
public class AuthorizerSteps extends Steps {

    @Step("Получить все проекты папки")
    public void getAllProjectFromFolder(String folderId) {
        ArrayList<String> projectId = new Http(AuthorizerURL)
                .get("folders/" + Objects.requireNonNull(folderId) + "/children")
                .assertStatus(200)
                .jsonPath()
                .get("data.name");
        log.info(projectId);
    }

    @Step("Получение пути до папки/проекта")
    public String getPathToFolder(String target) {
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
    public String getParentProject(String target) {
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

}

