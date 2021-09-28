package steps.portalBack;

import core.helper.Configure;
import core.helper.Http;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
public class AccessGroupSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    /**
     * Метод создает группу доступа в определенном проекте
     * @param projectName ID проекта
     * @param accessName  имя создаваемой группы доступа
     */
    @Step("Создание группы доступа в проекте {projectName} с названием {accessName}")
    public void createAccessGroup(String projectName, String accessName) {
        StringUtils stringUtils = new StringUtils();
        //Получаем проект из памяти по его ID
        Project project = cacheService.entity(Project.class)
                .withField("projectName", projectName)
                .forOrders(false)
                .getEntity();

        String accessNameBuild = accessName + stringUtils.getRandString(11);
        String groupName = jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", accessNameBuild)
                .set("$.access_group.project_name", project.id)
                .send(URL)
                //.setProjectId(project.id)
                .post(String.format("portal/api/v1/projects/%s/access_groups", project.id))
                .assertStatus(201)
                .jsonPath()
                .get("name");

        models.authorizer.AccessGroup accessGroup = models.authorizer.AccessGroup.builder()
                .name(groupName)
                .projectName(project.id)
                .user(null)
                .build();
        cacheService.saveEntity(accessGroup);
    }

    /**
     * Метод удаляет группу доступа по ID проекта
     * @param env среда
     */
    @Step("Удаление группы доступа в проекте {projectName}")
    public void deleteAccessGroup(String env) {
        //Получение проекта из памяти
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(false)
                .getEntity();
        //Плучение группы доступа из памяти
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        //Отправка запроса на удаление группы доступа
        new Http(URL)
                .setProjectId(project.id)
                .delete(String.format("portal/api/v1/projects/%s/access_groups/%s", project.id, accessGroup.name))
                .assertStatus(204)
                .jsonPath();
        //Проставление флага "группа доступа удалена"
        accessGroup.isDeleted = true;
        //Сохранение состояния группы доступа в память
        cacheService.saveEntity(accessGroup);
    }

    /**
     * Метод добавляет пользователя в группу доступа
     * @param env      имя среды
     * @param username пользователь
     */
    @Step("Добавление пользователя в группу доступа для проекта среды {env}")
    public void addUsersToGroup(String env, String username) {
        //Получение проекта из памяти
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(false)
                .getEntity();
        //Плучение группы доступа из памяти
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        String[] arr = new String[]{username};
        //Отправка запроса на добавление пользователя в группу доступа
        String response = jsonHelper.getJsonTemplate("/accessGroup/users.json")
                .set("$.users", arr)
                .send(URL)
                .post(String.format("portal/api/v1/projects/%s/access_groups/%s/group_users", project.id, accessGroup.name))
                .assertStatus(201)
                .jsonPath().getString("unique_name");

        //Отсечение скобок [] у пользователя
        String usernameFromResponse = response.substring(1, response.length() - 1);
        //Сравнение пользователя с пользователем из ответа на добавление
        Assertions.assertEquals(username, usernameFromResponse);
        //Сохранение группы доступа в память
        accessGroup.user = usernameFromResponse;
        cacheService.saveEntity(accessGroup);
    }

    @Step("Удаление пользователя из группы доступа для проекта среды {env}")
    public void deleteUsersFromGroup(String env) throws UnsupportedEncodingException {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(false)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        JsonPath jsonPath = new Http(URL)
                .delete(String.format("portal/api/v1/projects/%s/access_groups/%s/group_users?unique_name=%s", project.id, accessGroup.name, URLEncoder.encode(accessGroup.user, String.valueOf(StandardCharsets.UTF_8))))
                .assertStatus(204)
                .jsonPath();

        accessGroup.user = null;
    }
}
