package steps.resourceManager;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.restassured.path.json.JsonPath;
import steps.Steps;

import static core.helper.Configure.ResourceManagerURL;

public class ResourceManagerSteps extends Steps {

    public static Response getProjectById(String id, String include) {
        return new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}?include={}", id, include)
                .assertStatus(200);
    }

    public static JsonPath getProjectPath(String id) {
        return new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}?include=information_system,project_environment,environment_prefix,availability", id)
                .assertStatus(200)
                .jsonPath();
    }
}
