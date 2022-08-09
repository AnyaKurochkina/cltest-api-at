package ui.uiModels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.orderService.interfaces.IProduct;
import org.json.JSONObject;
import ui.t1.steps.AuthSteps;
import ui.t1.steps.MainSteps;
import ui.t1.steps.OrgStructureSteps;
import ui.t1.steps.OrganizationSteps;

import static core.helper.Configure.*;
import static core.helper.Configure.getAppProp;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class VmWareOrganization extends IProduct {
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
                .setRole(Role.T1_ADMIN)
                .post("/v1/projects/proj-4h86otzsew/vdc_organizations")
                .assertStatus(201)
                .jsonPath();
        organizationName = jsonPath.getString("name");
    }

    @Override
    protected void delete() {
        new Http(PortalBackURL)
                .setRole(Role.T1_ADMIN)
                .delete("/v1/projects/proj-4h86otzsew/vdc_organizations/" + organizationName)
                .assertStatus(204)
                .jsonPath();
    }

    protected void createUi() {
        //Логинимся
        AuthSteps authSteps = new AuthSteps();
        authSteps.signIn();
        //Выбираем организацию
        MainSteps mainSteps = new MainSteps();
        mainSteps.goToOrgStructure();
        OrgStructureSteps orgStructureSteps = new OrgStructureSteps();
        orgStructureSteps
                .chooseGlobalOrganization()
                .chooseProject();
        //Создаем VmWare организацию
        OrganizationSteps organizationSteps = new OrganizationSteps();
        organizationName = organizationSteps.createOrganization();
    }

    protected void deleteUi() {
        //Логинимся
        AuthSteps authSteps = new AuthSteps();
        authSteps.signIn();
        //Выбираем организацию
        MainSteps mainSteps = new MainSteps();
        mainSteps.goToOrgStructure();
        OrgStructureSteps orgStructureSteps = new OrgStructureSteps();
        orgStructureSteps
                .chooseGlobalOrganization()
                .chooseProject();
        //Удаялем VmWare организацию
        mainSteps.goToListOfOrganization();
        OrganizationSteps organizationSteps = new OrganizationSteps();
        organizationSteps.deleteOrganizationFromListOfOrganizations(organizationName);
    }
}
