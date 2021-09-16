package steps.authorizer;

import core.helper.Configure;
import core.helper.Http;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Folder;
import models.authorizer.InformationSystem;
import models.authorizer.Organization;
import steps.Steps;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;

@Log4j2
public class AuthorizerSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    @Step("Получить все проекты папки")
    public void getAllProjectFromFolder(String folderId) {
        ArrayList<String> projectId = new Http(URL)
                .get("authorizer/api/v1/folders/" + folderId + "/children")
                .assertStatus(200)
                .jsonPath()
                .get("data.name");
        System.out.println(projectId);
    }

    /**
     * @param folderType тип папки (Business, Department, Folder)
     * @param parentName имя родительской папки
     * @param name имя создаваемой папки
     */
    @Step("Создание папки типа {folderType} в родительской папке {parentName} с именем {name}")
    public void createFolder(String folderType, String parentName, String name) {
        Folder parentFolder = null;
        String id = "vtb";
        /*
        Проверка имени родительской папки (если её имя vtb, то оставляем родительскую папку пустой,
        а если имя другое, то получаем его из cacheService)
        */
        if (!parentName.equalsIgnoreCase("vtb")) {
            parentFolder = (cacheService.entity(Folder.class)
                    .withField("name", parentName)
                    .withField("isDeleted", false)
                    .getEntity());
            id = parentFolder.id;
        }
        //Получение информационной системы
        InformationSystem informationSystem = cacheService.entity(InformationSystem.class)
                .forOrders(false)
                .getEntity();
        //Отправка запроса на создание папки
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folderType)
                .set("$.folder.title", name)
                .set("$.folder.information_system_ids", new String[]{informationSystem.id})
                .send(URL)
                .post(parentName.equalsIgnoreCase("vtb")
                        ? "authorizer/api/v1/organizations/vtb/folders"
                        : String.format("authorizer/api/v1/folders/%s/folders", parentFolder.id))
                .assertStatus(201)
                .jsonPath();
        //Создание объекта папка с параметрами полученными выше
        Folder folder = Folder.builder()
                .id(jsonPath.get("data.name"))
                .type(folderType)
                .name(jsonPath.get("data.title"))
                .parentId(id)
                .build();
        //Сохранение папки
        cacheService.saveEntity(folder);
    }

    /**
     *
     * @param folderType тип папки
     * @param name имя папки
     */
    @Step("Удаление папки типа {folderType} с именем {name}")
    public void deleteFolder(String folderType, String name) {
        //Получение папки по ёё типу, имени и флагу "Не удалена"
        Folder folder = cacheService.entity(Folder.class)
                .withField("type", folderType)
                .withField("name", name)
                .withField("isDeleted", false)
                .getEntity();
        //Выполнение запроса на удаление папки
        new Http(URL)
                .delete("authorizer/api/v1/folders/" + folder.id)
                .assertStatus(204);
        //Проставление флага "Папка удалена"
        folder.isDeleted = true;
        //Сохранение текущего состояния папки
        cacheService.saveEntity(folder);
    }

    /**
     *
     * @param orgTitle имя организации
     */
    @Step("Получение организации")
    public void getOrgName(String orgTitle) {
        //Отправка запроса на получение организаций
        JsonPath jsonPath = new Http(URL)
                .get("authorizer/api/v1/organizations?page=1&per_page=25")
                .assertStatus(200)
                .jsonPath();
        //Получение конкретного ID организации
        String orgName = jsonPath.get(String.format("data.find{it.title=='%s'}.name", orgTitle));
        //Создание организации с параметрами полученными выше
        Organization organization = Organization.builder()
                .name(orgName)
                .title(orgTitle)
                .build();
        //Сохранение организации
        cacheService.saveEntity(organization);
    }

    @Step("Получение пути до папки/проекта")
    public String getPathToFolder(String target) {
        String url;
        if (target.startsWith("fold")) {
            url = "authorizer/api/v1/folders/" + target + "/path";
        } else if (target.startsWith("proj")) {
            url = "authorizer/api/v1/projects/" + target + "/path";
        } else {
            throw new Error("Invalid target: " + target + "\nYour target must start with \"fold\" or \"proj\"");
        }

        String path = new Http(URL)
                .get(url)
                .assertStatus(200)
                .jsonPath()
                .get("data.path");

        log.info("Путь до папки/проекта: " + path);
        return path;
    }



}

