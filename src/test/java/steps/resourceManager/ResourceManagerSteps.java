package steps.resourceManager;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import steps.Steps;

import static core.helper.Configure.ResourceManagerURL;

public class ResourceManagerSteps extends Steps {

    public static Response getProjectById(String id, String include) {
        return new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}?include={}", id, include)
                .assertStatus(200);
    }
}
