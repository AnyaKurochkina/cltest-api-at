package models.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.productOrgInfoSystem.createInfoSystem.CreateInfoSystemResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

import static core.helper.Configure.ProductCatalogDeprURL;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
@Builder
@Getter
public class ProductOrgInfoSystem extends Entity {

    private String product;
    private String id;
    private String organization;
    private List<String> informationSystems;
    private final String productName = "product_org_info_system/";
    private String jsonTemplate;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/productOrgInfoSystem/createInfoSystem.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.organization", organization)
                .set("$.product", product)
                .build();
    }

    @Override
    protected void create() {
        id = new Http(ProductCatalogDeprURL)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateInfoSystemResponse.class)
                .getId();
        assertNotNull(id, "ProductOrgInfoSystem не создался");
    }

    @Override
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + product + "/organizations/" + organization + "/")
                .assertStatus(204);
    }
}
