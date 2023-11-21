package models.cloud.orderService.products;

import com.google.gson.annotations.SerializedName;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Organization;
import models.cloud.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.authorizer.AuthorizerSteps;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class GitLab extends IProduct {
    @ToString.Include
    String groupName;
    @Builder.Default
    List<String> projects = new ArrayList<>();

    private static String XPATH_GROUP_VARIABLE = "data.find{it.data.config.variables.find{it.key=='%s'}} != null";
    private static String XPATH_TOKEN = "data.find{it.data.config.tokens.find{it.name=='%s'}} != null";

    @Override
    public Entity init() {
        jsonTemplate = "/orders/gitlab.json";
        productName = "Группа Gitlab";
        initProduct();
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @SneakyThrows
    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.group_name", "vtb-" + new Random().nextInt())
                .set("$.order.label", getLabel())
                .build();
    }

    @Step("Создать CI/CD переменную для GitLab группы")
    public void createVariable(Variable attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("create_gitlab_group_variable").product(this)
                .data(new JSONObject(JsonHelper.toJson(attrs))).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(XPATH_GROUP_VARIABLE, attrs.key)), "Переменная не создалась");
    }

    @Step("Обновить переменную")
    public void updateVariable(Variable attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_gitlab_variable").product(this)
                .data(new JSONObject(JsonHelper.toJson(attrs))).build());
    }

    @Step("Удалить переменную")
    public void deleteVariable(Variable attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("delete_gitlab_variable").product(this)
                .data(new JSONObject(JsonHelper.toJson(attrs))).build());

        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(XPATH_GROUP_VARIABLE, attrs.key)), "Переменная не удалилась");
    }

    @Step("Добавить участника к группе GitLab")
    public void addUser(String userName, String accessLevel) {
        JSONArray users = new JSONArray(AuthorizerSteps.findUsers(userName, Organization.builder().type("default").build().createObject()));
        Iterator<Object> iterator = users.iterator();
        while (iterator.hasNext()) {
            JSONObject user = (JSONObject) iterator.next();
            user.put("name", user.get("firstname"));
            user.remove("firstname");
        }
        OrderServiceSteps.runAction(ActionParameters.builder().name("add_user_to_gitlab_group").product(this)
                .data(new JSONObject().put("access_level", accessLevel).put("users", users)).build());

    }

    @Step("Изменить пользователя")
    public void updateUser(JSONObject user, String role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_gitlab_user").product(this).data(user.put("role", role)).build());
    }

    @Step("Удалить пользователя")
    public void deleteUser(JSONObject user) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("delete_gitlab_user").product(this).data(user).build());
    }

    @Step("Создать проект GitLab")
    public void createProject(Project project) {
        if (projects.contains(project.name))
            return;
        OrderServiceSteps.runAction(ActionParameters.builder().name("create_gitlab_project").product(this)
                .data(new JSONObject(JsonHelper.toJson(project))).build());
        projects.add(project.name);
        save();
    }

    @Step("Создать CI/CD переменную для GitLab проекта")
    public void createProjectVariable(Variable attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("create_gitlab_project_variable").product(this)
                .data(new JSONObject(JsonHelper.toJson(attrs))).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(XPATH_GROUP_VARIABLE, attrs.key)), "Переменная не создалась");
    }

    @Step("Обновить переменную для GitLab проекта")
    public void updateProjectVariable(Variable attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_gitlab_variable").product(this)
                .data(new JSONObject(JsonHelper.toJson(attrs)).put("environment_scope", "*")).build());
    }

    @Step("Удалить переменную для GitLab проекта")
    public void deleteProjectVariable(Variable attrs) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("delete_gitlab_variable").product(this).data(new JSONObject(JsonHelper.toJson(attrs))).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(XPATH_GROUP_VARIABLE, attrs.key)), "Переменная не удалилась");
    }

    @Step("Создать токен")
    public void createProjectToken(String name, List<String> scopes) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("create_project_token_gitlab").product(this)
                .data(new JSONObject().put("name", name).put("scopes", new JSONArray(scopes))).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(XPATH_TOKEN, name)), "Токен не создался");
    }

    @Step("Отозвать токен")
    public void deleteProjectToken(JSONObject token) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("delete_project_token_gitlab").product(this).data(token).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(XPATH_TOKEN, token.get("name"))), "Токен не был удален");
    }

    @Step("Удалить проект GitLab")
    public void deleteProject(Project project) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("delete_gitlab_project").product(this).data(new JSONObject()).build());
        projects.remove(project.name);
        save();
    }


    @Builder
    public static class Variable {
        String key;
        boolean masked;
        @SerializedName("protected")
        boolean protectedVar;
        String value;
    }

    @Builder
    public static class Project {
        String description;
        String name;
        String visibility;
    }

    @Override
    @Step("Удаление продукта")
    protected void delete() {
        delete("delete_gitlab_group");
    }
}
