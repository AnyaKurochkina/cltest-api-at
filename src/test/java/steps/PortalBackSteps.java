package steps;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.ShareData;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;


@Log4j2
public class PortalBackSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Получение информационных систем")
    public void getInfoSys(String sysName) {
        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/organizations/%s/information_systems?page=1&per_page=100&include=total_count", ShareData.getString("orgName")))
                .assertStatus(200)
                .jsonPath();

        int countOfIteration = (int)jsonPath.get("meta.total_count")/ 100 + 1;

        for (int i = 1; i <=countOfIteration; i++){
            JsonPath jsonPathInCycle = new Http(URL)
                    .get(String.format("portal/api/v1/organizations/%s/information_systems?page=%s&per_page=100&include=total_count", ShareData.getString("orgName"), i))
                    .assertStatus(200)
                    .jsonPath();

            if(jsonPathInCycle.get(String.format("list.find{it.code=='%s'}.id", sysName)) != null){
                ShareData.put("infoSystemId", jsonPathInCycle.get(String.format("list.find{it.code=='%s'}.id", sysName)));
                break;
            }
        }
    }

    @Step("Получение ID project env")
    public void getProjectEnv(String envName) {
        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/folders/%s/information_systems/%s/project_environments?page=1&per_page=100&include=total_count", ShareData.getString("folder_id_default"), ShareData.getString("infoSystemId")))
                .assertStatus(200)
                .jsonPath();

        int countOfIteration = (int)jsonPath.get("meta.total_count")/ 100 + 1;
        for (int i = 1; i <=countOfIteration; i++){
            JsonPath jsonPathInCycle = new Http(URL)
                    .get(String.format("portal/api/v1/folders/%s/information_systems/%s/project_environments?page=%s&per_page=100&include=total_count", ShareData.getString("folder_id_default"),ShareData.getString("infoSystemId"), i))
                    .assertStatus(200)
                    .jsonPath();

            if(jsonPathInCycle.get(String.format("list.find{it.purpose=='%s'}.id", envName)) != null){
                ShareData.put("ProjEnvId", jsonPathInCycle.get(String.format("list.find{it.purpose=='%s'}.id", envName)));
                break;
            }
        }
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