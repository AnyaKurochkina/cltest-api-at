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
public class AccessGroup extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание группы доступа с названием{access_name} и сохранение в переменную {param}")
    public void CreateAccessGroup(String env, String access_name, String param) {
        String projectId = ShareData.get((String.format("projects.find{it.env == '%s'}.id", env)));
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", access_name)
                .set("$.access_group.project_name", projectId)
                .send(URL)
                .post(String.format("portal/api/v1/projects/%s/access_groups", projectId))
                .assertStatus(201)
                .jsonPath();

        String group_name = jsonPath.get("name");
        testVars.setVariables("group_name", group_name);
        ShareData.put("projects[?(@.env =='DEV')].group_name", group_name);
    }
}