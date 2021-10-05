package steps.authorizer;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.authorizer.InformationSystem;
import models.authorizer.ProjectEnvironment;
import org.junit.Assert;
import models.authorizer.Folder;
import models.authorizer.Project;
import steps.Steps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class ProjectSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    /**
     *
     * @param folderName имя папки
     * @param projectName имя проекта
     * @param env среда
     */
    @Step("Создание проекта в папке {folderName} с названием {projectName}")
    public void createProject(String folderName, String projectName, String env) {
        //Получение папки по её имени и флагу "Папка не удалена"
        Folder folder = cacheService.entity(Folder.class)
                .withField("name", folderName)
                .withField("isDeleted", false)
                .getEntity();
        //Получение информационной системы
        InformationSystem informationSystem = cacheService.entity(InformationSystem.class)
                .forOrders(false)
                .getEntity();
        //Получение среды в которой будет создаваться проект
        ProjectEnvironment projectEnvironment = cacheService.entity(ProjectEnvironment.class)
                .withField("env", env)
                .forOrders(false)
                .getEntity();
        //Получение рандомного префикса среды из доступных
        String prefix = getPrefixEnv(folder.getName(), informationSystem.id, projectEnvironment.id);
        //Отправление запроса на создание проекта с получение его ID
        String projectId = jsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", projectName)
                .set("$.project.information_system_id", informationSystem.id)
                .set("$.project.project_environment_id", projectEnvironment.id)
                .set("$.project.environment_prefix_id", prefix)
                .send(URL)
                .post(String.format("authorizer/api/v1/folders/%s/projects", folder.getName()))
                .assertStatus(201)
                .jsonPath()
                .get("data.name");

        //Создание объекта проект с параметрами полученными выше
        Project project = Project.builder()
                .projectName(projectName)
                .id(projectId)
                .informationSystem(informationSystem.id)
                .env(env)
                .build();
        //Сохранение проекта
        cacheService.saveEntity(project);
    }

    /**
     *
     * @param env среда проекта
     */
    @Step("Удаление проекта с названием {env}")
    public void deleteProject(String env) {
        //Получение проекта по названию среды
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(false)
                .getEntity();
        //Отправка запроса на удаление проекта
        new Http(URL)
                .delete("authorizer/api/v1/projects/" + project.id)
                .assertStatus(204);
        //Сохранение флага "Проект удалён"
        project.isDeleted = true;
        //Сохранение текущего состояния проекта
        cacheService.saveEntity(project);
    }

    /**
     *
     * @param projectId ID проекта
     * @param infoSystems информационная система
     * @param projectEnvId ID среды проекта
     * @return - возвращаем рандомный префикс из доступных
     */
    public String getPrefixEnv(String projectId, String infoSystems, String projectEnvId) {
        //Получение префикосв среды
        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/folders/%s/information_systems/%s/environment_prefixes?project_environment_id=%s&reserved=false", projectId, infoSystems, projectEnvId))
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
            Assert.fail("Количество префиксов <=0");
        }
        return jsonPath.get("list[" + i + "].id");
    }
}
