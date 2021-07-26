package tests.suites.Authorizer;

import core.helper.*;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import tests.suites.Authorizer.models.Folder;
import tests.suites.Steps;

public class AuthorizerSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание папки типа {folderType} в родительской папке {parentName} с именем {name}")
    public void createFolder(String folderType, String parentName, String name) {
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folderType)
                .set("$.folder.title", name)
                .send(URL)
                .post(parentName.equalsIgnoreCase("vtb")
                        ? "authorizer/api/v1/organizations/vtb/folders"
                        : String.format("authorizer/api/v1/folders/%s/folders", ((Folder) cacheService.entity(Folder.class)
                        .setField("name", parentName)
                        .setField("isDeleted", false)
                        .getEntity())
                        .id
                ))
                .assertStatus(201)
                .jsonPath();

        Folder folder = Folder.builder()
                .id(jsonPath.get("data.name"))
                .type(folderType)
                .name(jsonPath.get("data.title"))
                .build();
        cacheService.saveEntity(Folder.class, folder);
    }

    @Step("Удаление папки типа {folderType} с именем {name}")
    public void deleteFolder(String folderType, String name) {
        Folder folder = cacheService.entity(Folder.class)
                .setField("type", folderType)
                .setField("name", name)
                .setField("isDeleted", false)
                .getEntity();

        jsonHelper.getJsonTemplate("/structure/create_folder.json")
                .set("$.folder.kind", folderType)
                .set("$.folder.title", name)
                .send(URL)
                .delete("authorizer/api/v1/folders/" + folder.id)
                .assertStatus(204);

        folder.isDeleted = true;
        //cacheService.setEntity(Folder.class, f);
    }

}

