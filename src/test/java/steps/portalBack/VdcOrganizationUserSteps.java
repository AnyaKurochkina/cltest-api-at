package steps.portalBack;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.portalBack.VmWareOrganizationUser;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.PortalBackURL;

public class VdcOrganizationUserSteps extends Steps {

    @Step("Создание VMware user {userName}")
    public static VmWareOrganizationUser createVMwareUser(String userName, String projectId, String orgName) {
        JSONObject jsonObject = VmWareOrganizationUser.builder()
                .username(userName)
                .build()
                .init()
                .toJson();
        return new Http(PortalBackURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(jsonObject)
                .post("/v1/projects/{}/vdc_organizations/{}/vdc_organization_users", projectId, orgName)
                .assertStatus(201)
                .extractAs(VmWareOrganizationUser.class);
    }
}
