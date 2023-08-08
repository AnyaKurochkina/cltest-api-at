package steps.resourceManager;

import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import steps.Steps;

import java.util.Objects;

import static core.helper.Configure.ResourceManagerURL;

public class ResourceManagerSteps extends Steps {

    public static Response getProjectById(String id, String include) {
        return new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}?include={}", id, include)
                .assertStatus(200);
    }

    public static JsonPath getProjectJsonPath(String id) {
        return new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}?include=information_system,project_environment,environment_prefix,availability", id)
                .assertStatus(200)
                .jsonPath();
    }

    public static String getProjectPath(String type, String id) {
        return Objects.requireNonNull(new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/{}/{}/path", type, id)
                .assertStatus(200)
                .jsonPath()
                .getString("data.path"));
    }

    @Step("Удаление папки с именем {name}")
    public static void deleteFolder(String name) {
        new Http(Configure.ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .delete("/v1/folders/" + name)
                .assertStatus(204);
    }
}
