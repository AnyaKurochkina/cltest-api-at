package models.portalBack;

import com.mifmif.common.regex.Generex;
import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import models.authorizer.Project;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class AccessGroup extends Entity {
    String prefixName;
    String name;
    String projectName;
    String description;
    List<String> users;

    @Step("Добавление пользователя в группу доступа")
    public void addUser(String user){
        users.add(user);
        save();
    }

    @Step("Удаление пользователя из группы доступа")
    public void removeUser(String user){
        users.remove(user);
        save();
    }

    @Override
    public Entity init() {
        if (name == null)
            name = new Generex("[a-z]{5,15}").random();
        if (projectName == null)
            projectName = ((Project) Project.builder().isForOrders(false).build().createObject()).getId();
        if (description == null)
            description = projectName;
        if(users == null)
            users = new ArrayList<>();
        return this;
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", name)
                .set("$.access_group.description", description)
                .set("$.access_group.project_name", projectName)
                .build();
    }

    @Override
    @Step("Создание группы доступа")
    protected void create() {
        prefixName = new Http(Configure.PortalBackURL)
                .body(toJson())
                .post("projects/{}/access_groups", projectName)
                .assertStatus(201)
                .jsonPath()
                .getString("name");
    }

    @Step("Редактирование группы доступа")
    public void editGroup(String newDescription) {
        description = new Http(Configure.PortalBackURL)
                .body(String.format("{\"access_group\":{\"description\":\"%s\"}}", newDescription))
                .patch("projects/{}/access_groups/{}", projectName, prefixName)
                .assertStatus(200)
                .jsonPath()
                .getString("description");
        Assertions.assertEquals(newDescription, description, "Описание не изменилось: " + description);
    }

    @Override
    @Step("Удаление группы доступа")
    protected void delete() {
        new Http(Configure.PortalBackURL)
                .delete(String.format("projects/%s/access_groups/%s", projectName, prefixName))
                .assertStatus(204)
                .jsonPath();
    }

}
