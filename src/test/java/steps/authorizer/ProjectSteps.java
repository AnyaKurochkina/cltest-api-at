package steps.authorizer;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static core.helper.Configure.PortalBackURL;

public class ProjectSteps extends Steps {

    /**
     *
     * @param projectId ID проекта
     * @param infoSystems информационная система
     * @param projectEnvId ID среды проекта
     * @return - возвращаем рандомный префикс из доступных
     */
    public String getPrefixEnv(String projectId, String infoSystems, String projectEnvId) {
        //Получение префикосв среды
        JsonPath jsonPath = new Http(PortalBackURL)
                .get(String.format("folders/%s/information_systems/%s/environment_prefixes?project_environment_id=%s&reserved=false",
                        Objects.requireNonNull(projectId), Objects.requireNonNull(infoSystems), Objects.requireNonNull(projectEnvId)))
                .assertStatus(200)
                .jsonPath();
        //Сохранение списка префиксов с параметрами в список
        List<LinkedHashMap<String, String>> prefix_list = jsonPath.get("list");
        //Возвращаем рандомный префикс из доступных
        Random rand = new Random();
        int i = 0;
        try {
            i = rand.nextInt(prefix_list.size());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Assertions.fail("Количество префиксов <=0");
        }
        return jsonPath.get("list[" + i + "].id");
    }
}
