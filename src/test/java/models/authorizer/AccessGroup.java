package models.authorizer;

import core.helper.Configure;
import core.helper.Http;
import core.random.string.RandomStringGenerator;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import models.Entity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class AccessGroup extends Entity {
    String name;
    String projectName;
    @Singular
    List<String> users;
    Boolean isForOrders;

    public void addUser(String user){
        users.add(user);
        save();
    }

    public void removeUser(String user){
        users.remove(user);
        save();
    }

    @Override
    public void init() {
        if (name == null)
            name = new RandomStringGenerator().generateByRegex("[a-z]{5,15}");
        if (projectName == null)
            projectName = ((Project) Project.builder().isForOrders(false).build().createObject()).getId();
    }

    public JSONObject toJson() {
        return jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", name)
                .set("$.access_group.project_name", projectName)
                .build();
    }

    @Override
    @Step("Создание группы доступа")
    public void create() {
        name = new Http(Configure.PortalBackURL)
                .post(String.format("projects/%s/access_groups", projectName), toJson())
                .assertStatus(201)
                .jsonPath()
                .getString("name");
    }

    @Override
    @Step("Удаление группы доступа")
    public void delete() {
        new Http(Configure.PortalBackURL)
                .delete(String.format("projects/%s/access_groups/%s", projectName, name))
                .assertStatus(204)
                .jsonPath();
    }

}
