package steps;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.ShareData;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.junit.Test;


@Log4j2
public class AuthorizerSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");
    private final PortalBackSteps portalBackSteps = new PortalBackSteps();

    @Step("Получение имени организации")
    public void getOrgName(String orgTitle) {
        JsonPath jsonPath = new Http(URL)
                .get(String.format("authorizer/api/v1/organizations?page=1&per_page=25"))
                .assertStatus(200)
                .jsonPath();

        String orgName = jsonPath.get(String.format("data.find{it.title=='%s'}.name", orgTitle));
        ShareData.put("orgName", orgName);
    }

    @Test
    public void test() {
        System.out.println();
    }


    @Step("Создание папки типа {folder_type} в родительской папке {parentName} с именем {name} и сохраняем в переменную ее ID {param}")
    public void createFolder(String folder_type, String parentName, String name, String param) {
        log.info("Изменение базового шаблона запроса при создании папки с типом: " + folder_type);

        JsonPath jsonPath = jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folder_type)
                .set("$.folder.title", name)
                .set("$.folder.information_system_ids.[0]", ShareData.get("infoSystemId"))
                .send(URL)
                .post(parentName.equalsIgnoreCase("vtb")
                        ? "authorizer/api/v1/organizations/vtb/folders"
                        : String.format("authorizer/api/v1/folders/%s/folders", ShareData.getString(parentName)))
                .assertStatus(201)
                .jsonPath();

        testVars.setVariables("businessBlockTitle", jsonPath.get("data.title"));
        String folder_id = jsonPath.get("data.name");
        ShareData.put(param, folder_id);
    }

    @Step("Создать проект с именем {project_name} для родительской папки {folder_id}")
    public void createProject(String project_name, String folder_id) {
        String prefix = portalBackSteps.getPrefixEnv(folder_id, ShareData.getString("infoSystemId"), ShareData.getString("ProjEnvId"));
        log.info("Folder ID: " + ShareData.getString(folder_id));

        String project_id = jsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", project_name)
                .set("$.project.information_system_id", ShareData.getString("infoSystemId"))
                .set("$.project.project_environment_id", ShareData.getString("ProjEnvId"))
                .set("$.project.environment_prefix_id", prefix)
                .send(URL)
                .post(String.format("authorizer/api/v1/folders/%s/projects", ShareData.getString(folder_id)))
                .assertStatus(201)
                .jsonPath()
                .get("data.name");


        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", project_id);
        jsonObj.put("env", "DEV");
        ShareData.putArray("projects", jsonObj);
    }

}