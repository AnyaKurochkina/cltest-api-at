package steps.portalBack;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.portalBack.VmWareOrganization;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.VcloudConnector;

public class VdcOrganizationSteps extends Steps {

    @Step("Создание VMware организации")
    public static VmWareOrganization createVMwareOrganization(String name, String projectId) {
        JSONObject organization = VmWareOrganization.builder()
                .name(name)
                .projectName(projectId)
                .build()
                .init()
                .toJson();
        return new Http(VcloudConnector)
                .setRole(Role.CLOUD_ADMIN)
                .body(organization)
                .post("api/v1/projects/{}/vdc_organizations", projectId)
                .assertStatus(201)
                .extractAs(VmWareOrganization.class);
    }

    @Step("Удаление VMware организации c именем {orgName}")
    public static void deleteVMwareOrganization(String project, String orgName) {
        new Http(VcloudConnector)
                .setRole(Role.CLOUD_ADMIN)
                .delete("api/v1/projects/{}/vdc_organizations/{}", project, orgName)
                .assertStatus(204);
    }
}
