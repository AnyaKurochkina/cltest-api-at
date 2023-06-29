package steps.rpcRouter;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class OutputQueueSteps extends Steps {

    private static final String outPutQueueV1 = "/api/v1/output_queues/";

    @Step("Удаление OutPutQueue")
    public static Response deleteOutPutQueue(Integer id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(outPutQueueV1 + "{}/", id)
                .assertStatus(204);
    }

    @Step("Создание OutPutQueue")
    public static Response createOutPutQueue(JSONObject jsonObject) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post(outPutQueueV1)
                .assertStatus(201);
    }
}
