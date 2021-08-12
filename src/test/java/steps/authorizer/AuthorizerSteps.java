package steps.authorizer;

import core.helper.*;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Folder;
import models.authorizer.Organization;
import steps.Steps;

@Log4j2
public class AuthorizerSteps extends Steps {
    private static final String URL = Configure.getInstance().getAppProp("host_kong");

    @Step("Создание папки типа {folderType} в родительской папке {parentName} с именем {name}")
    public void createFolder(String folderType, String parentName, String name) {
        Folder parentFolder = null;
        if (!parentName.equalsIgnoreCase("vtb")) {
            parentFolder = ((Folder) cacheService.entity(Folder.class)
                    .withField("name", parentName)
                    .withField("isDeleted", false)
                    .getEntity());
        }
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folderType)
                .set("$.folder.title", name)
                .send(URL)
                .post(parentName.equalsIgnoreCase("vtb")
                        ? "authorizer/api/v1/organizations/vtb/folders"
                        : String.format("authorizer/api/v1/folders/%s/folders", parentFolder.id))
                .assertStatus(201)
                .jsonPath();

        String id = "vtb";
        if (!parentName.equalsIgnoreCase("vtb")) {
            id = parentFolder.id;
        }

        Folder folder = Folder.builder()
                .id(jsonPath.get("data.name"))
                .type(folderType)
                .name(jsonPath.get("data.title"))
                .parentId(id)
                .build();
        cacheService.saveEntity(folder);
    }

    @Step("Удаление папки типа {folderType} с именем {name}")
    public void deleteFolder(String folderType, String name) {
        Folder folder = cacheService.entity(Folder.class)
                .withField("type", folderType)
                .withField("name", name)
                .withField("isDeleted", false)
                .getEntity();

        new Http(URL)
                .delete("authorizer/api/v1/folders/" + folder.id)
                .assertStatus(204);

        folder.isDeleted = true;
        cacheService.saveEntity(folder);
    }

    @Step("Получение имени организации")
    public void getOrgName(String orgTitle) {
        JsonPath jsonPath = new Http(URL)
                .get(String.format("authorizer/api/v1/organizations?page=1&per_page=25"))
                .assertStatus(200)
                .jsonPath();
        String orgName = jsonPath.get(String.format("data.find{it.title=='%s'}.name", orgTitle));
        Organization organization = Organization.builder()
                .name(orgName)
                .title(orgTitle)
                .build();
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

