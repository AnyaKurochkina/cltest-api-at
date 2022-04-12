package ui.uiModels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Organization;
import models.authorizer.Project;
import models.orderService.interfaces.ProductStatus;
import models.portalBack.AccessGroup;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import steps.orderService.OrderServiceSteps;
import ui.uiSteps.AuthSteps;
import ui.uiSteps.MainSteps;
import ui.uiSteps.OrganizationSteps;

import static core.helper.Configure.*;
import static core.helper.Configure.getAppProp;

//@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
//@EqualsAndHashCode(callSuper = true)
//@Log4j2
//@Data
//@NoArgsConstructor
//@SuperBuilder
public class VmWareOrganization extends Entity {
    @JsonIgnore
    private static final String login = getAppProp("user.login");
    @JsonIgnore
    private static final String password = getAppProp("user.password");
    private String organizationName;

    @Override
    public Entity init() {
        return null;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/t1/createOrg.json")
                .set("$.vdc_organization.name", "sdfsdfsdfs")
                .build();
    }

    @Override
    protected void create() {
        JsonPath jsonPath = new Http(PortalBackURL)
                .body(toJson())
                .setRole(Role.T1ADMIN)
                .post("projects/proj-4h86otzsew/vdc_organizations")
                .assertStatus(201)
                .jsonPath();
        organizationName = jsonPath.getString("name");
    }

    @Test
    protected void testec() {

        JsonPath jsonPath = new Http(PortalBackURL)
                .body(toJson())
                .setRole(Role.T1ADMIN)
                .post("projects/proj-4h86otzsew/vdc_organizations")
                .assertStatus(201)
                .jsonPath();

        organizationName = jsonPath.getString("name");

        new Http(PortalBackURL)
                .setRole(Role.T1ADMIN)
                .delete("projects/proj-4h86otzsew/vdc_organizations/" + organizationName)
                .assertStatus(204)
                .jsonPath();
        System.out.println();
    }

    @Override
    protected void delete() {
        new Http(PortalBackURL)
                .setRole(Role.T1ADMIN)
                .delete("projects/proj-4h86otzsew/vdc_organizations/" + organizationName)
                .assertStatus(204)
                .jsonPath();
    }

    protected void createUi() {
        AuthSteps authSteps = new AuthSteps();
        authSteps.signIn(login, password);
        OrganizationSteps organizationSteps = new OrganizationSteps();
        organizationName = organizationSteps.createOrganization();
    }

    protected void deleteUi() {
        AuthSteps authSteps = new AuthSteps();
        authSteps.signIn(login, password);
        MainSteps mainSteps = new MainSteps();
        mainSteps.createOrganization();
        OrganizationSteps organizationSteps = new OrganizationSteps();
        organizationSteps.deleteOrganizationFromListOfOrganizations(organizationName);
    }
}
