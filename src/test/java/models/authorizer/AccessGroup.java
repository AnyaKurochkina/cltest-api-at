package models.authorizer;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import core.random.string.RandomStringGenerator;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class AccessGroup extends Entity {
    String name;
    String projectName;
    String description;
    List<String> users;

    public void addUser(String user){
        users.add(user);
        save();
    }

    public void removeUser(String user){
        users.remove(user);
        save();
    }

    @Override
    public Entity init() {
        if (name == null)
            name = new RandomStringGenerator().generateByRegex("[a-z]{5,15}");
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
        name = new Http(Configure.PortalBackURL)
                .body(toJson())
                .post(String.format("projects/%s/access_groups", projectName))
                .assertStatus(201)
                .jsonPath()
                .getString("name");
    }

    @Override
    @Step("Удаление группы доступа")
    protected void delete() {
        new Http(Configure.PortalBackURL)
                .delete(String.format("projects/%s/access_groups/%s", projectName, name))
                .assertStatus(204)
                .jsonPath();
    }

}
