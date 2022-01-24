package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.portalBack.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.Db;
import models.subModels.DbUser;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class PostgreSQL extends IProduct {
    private final static String DB_NAME_PATH = "data.find{it.config.containsKey('dbs')}.config.dbs.any{it.db_name=='%s'}";
//    private final static String DB_SIZE_PATH = "data.find{it.type=='app'}.config.dbs.size()";
    private final static String DB_USERNAME_PATH = "data.find{it.config.containsKey('db_users')}.config.db_users.any{it.user_name=='%s'}";
//    private final static String DB_USERNAME_SIZE_PATH = "data.find{it.type=='app'}.config.db_users.size()";
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String osVersion;
    @ToString.Include
    String postgresqlVersion;
    String domain;
    @Builder.Default
    public List<Db> database = new ArrayList<>();
    @Builder.Default
    public List<DbUser> users = new ArrayList<>();
    Flavor flavor;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/postgresql.json";
        productName = "PostgreSQL";
        initProduct();
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        List<Flavor> flavorList = referencesStep.getProductFlavorsLinkedList(this);
        flavor = flavorList.get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.postgresql_version", postgresqlVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .set("$.order.label", getLabel())
                .build();
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point", "/pg_data", 10);
    }

    public void createDb(String dbName) {
        if(database.contains(new Db(dbName)))
            return;
        orderServiceSteps.executeAction("create_db", this, new JSONObject(String.format("{db_name: \"%s\", db_admin_pass: \"KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq\"}", dbName)));
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)),
                "База данных не создалась c именем " + dbName);
        database.add(new Db(dbName));
        log.info("database = " + database);
        save();
    }

    //Удалить БД
    public void removeDb(String dbName) {
        orderServiceSteps.executeAction("remove_db", this, new JSONObject("{\"db_name\": \"" + dbName + "\"}"));
        Assertions.assertFalse((Boolean) orderServiceSteps.getProductsField(this, String.format(DB_NAME_PATH, dbName)));
        database.removeIf(db -> db.getNameDB().equals(dbName));
        save();
    }

    public void createDbmsUser(String username, String dbRole, String dbName) {
        orderServiceSteps.executeAction("create_dbms_user", this, new JSONObject(String.format("{\"comment\":\"testapi\",\"db_name\":\"%s\",\"dbms_role\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ\"}", dbName, dbRole, username)));
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(
                        this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                "Имя пользователя отличается от создаваемого");
        users.add(new DbUser(dbName, username, false));
        log.info("users = " + users);
        save();
    }

    //Сбросить пароль пользователя
    public void resetPassword(String username) {
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction("reset_db_user_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", username, password)));
    }

    //Сбросить пароль владельца
    public void resetDbOwnerPassword(String dbName) {
        Assertions.assertTrue(database.stream().anyMatch(db -> db.getNameDB().equals(dbName)), String.format("Базы %s не существует", dbName));
        String password = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
        orderServiceSteps.executeAction("reset_db_owner_password", this, new JSONObject(String.format("{\"user_name\":\"%s\",\"user_password\":\"%s\"}", dbName + "_admin", password)));
    }

    //Удалить пользователя
    public void removeDbmsUser(String username, String dbName) {
        orderServiceSteps.executeAction("remove_dbms_user", this, new JSONObject(String.format("{\"user_name\":\"%s\"}", String.format("%s_%s", dbName, username))));
        Assertions.assertFalse((Boolean) orderServiceSteps.getProductsField(
                this, String.format(DB_USERNAME_PATH, String.format("%s_%s", dbName, username))),
                String.format("Пользователь: %s не удалился из базы данных: %s", String.format("%s_%s", dbName, username), dbName));
        log.info("users = " + users);
        save();
    }

    public void resize(Flavor flavor) {
        orderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}"));
        int cpusAfter = (Integer) orderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter);
        Assertions.assertEquals(flavor.data.memory, memoryAfter);
    }

    public void restart() {
        restart("reset_two_layer");
    }

    public void stopSoft() {
        stopSoft("stop_two_layer");
    }

    public void start() {
        start("start_two_layer");
    }

    public void stopHard() {
        stopHard("stop_hard_two_layer");
    }
}




