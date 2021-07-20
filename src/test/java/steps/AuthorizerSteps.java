package steps;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.ShareData;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.junit.Assert;

import java.util.List;
import java.util.Random;

import static core.helper.JsonHelper.shareData;
import static org.junit.jupiter.api.Assertions.fail;


@Log4j2
public class AuthorizerSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание папки {folder_type} и сохраняем в переменную {param}")
    public void createBusinessBlock(String folder_type, String param) {
        log.info("Изменение базового шаблона запроса при создании папки с типом: " + folder_type);

        JsonPath jsonPath = jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folder_type)
                .set("$.folder.title", folder_type)
                .send(URL)
                .post("authorizer/api/v1/organizations/vtb/folders")
                .assertStatus(201)
                .jsonPath();

        String title = jsonPath.get("data.title");
        testVars.setVariables("businessBlockTitle", title);
        String folder_id = jsonPath.get("data.name");
        ShareData.put(param, folder_id);
    }

    @Step("Создание папки типа {folder_type} в родительской папке {parentName} с именем {name} и сохраняем в переменную ее ID {param}")
    public void createFolder(String folder_type, String parentName, String name, String param) {
        log.info("Изменение базового шаблона запроса при создании папки с типом: " + folder_type);

        JsonPath jsonPath = jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folder_type)
                .set("$.folder.title", name)
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
    public void createProject(String project_name, String folder_id)  {
        String infoSystems = jsonHelper.getTestDataFieldValue("structure/projectEnvironmentsDEV.json", "DEV", "information_systems");
        String projectEnvId = jsonHelper.getTestDataFieldValue("structure/projectEnvironmentsDEV.json", "DEV", "project_environment_id");
        String prefix = getPrefixEnv(folder_id, infoSystems, projectEnvId);
        log.info("Folder ID: " + ShareData.getString(folder_id));

        String project_id = jsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", project_name)
                .set("$.project.information_system_id", infoSystems)
                .set("$.project.project_environment_id", projectEnvId)
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

    public String getPrefixEnv(String folder_id, String infoSystems, String projectEnvId) {
        log.info(String.format("Получение префикса для ИС: %s и ProjectEnv: %s для контекста %s", infoSystems, projectEnvId, ShareData.getString(folder_id)));
        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/folders/%s/information_systems/%s/environment_prefixes?project_environment_id=%s&reserved=false", ShareData.getString(folder_id), infoSystems, projectEnvId))
                .assertStatus(200)
                .jsonPath();
        List prefix_list = jsonPath.get("list");
        Random rand = new Random();
        int i = 0;
        try {
            i = rand.nextInt(prefix_list.size());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Assert.fail("Количество префиксов <=0");
        }
        String prefix = jsonPath.get("list[" + i + "].id");
        log.info(String.format("Полученный префикс %s", prefix));
        return prefix;
    }

}