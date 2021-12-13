package steps.stateService;

import core.helper.Http;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.StateServiceURL;

@Log4j2
public class StateServiceSteps extends Steps {

    public String GetErrorFromStateService(IProduct product) throws JsonPathException {
        List<String> traceback = null;
        try {
            traceback = new Http(StateServiceURL)
                    .setProjectId(product.getProjectId())
                    .get("actions/?order_id=" + product.getOrderId())
                    .jsonPath().get("list.findAll{it.status.contains('error')}.data.traceback");
        } catch (JsonPathException e) {
            log.error(e.getMessage());
        }
        return String.valueOf(traceback);
    }
}
