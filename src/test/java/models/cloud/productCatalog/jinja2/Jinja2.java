package models.cloud.productCatalog.jinja2;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import httpModels.productCatalog.jinja2.getJinjaListResponse.GetJinjaListResponse;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import static steps.productCatalog.Jinja2Steps.isJinja2Exists;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "productName", callSuper = false)
@ToString(exclude = "productName")
public class Jinja2 extends Entity implements IProductCatalog {

    @JsonProperty("jinja2_template")
    private String jinja2Template;
    @JsonProperty("jinja2_data")
    private Object jinja2Data;
    private String name;
    private String description;
    private String id;
    private String title;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    private final String productName = "/api/v1/jinja2_templates/";

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/jinja2/createJinja.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.jinja2Template", jinja2Template)
                .build();
    }

    @Override
    @Step("Создание jinja2")
    protected void create() {
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, "productCatalog/jinja2/createJinja.json");
        if (isJinja2Exists(name)) {
            productCatalogSteps.deleteByName(name, GetJinjaListResponse.class);
        }
        Jinja2 jinja2 = new Http(Configure.ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(Jinja2.class);
        StringUtils.copyAvailableFields(jinja2, this);
        Assertions.assertNotNull(id, "Jinja с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление jinja2")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + id + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, "productCatalog/jinja2/createJinja.json");
        Assertions.assertFalse(productCatalogSteps.isExists(name));
    }
}
