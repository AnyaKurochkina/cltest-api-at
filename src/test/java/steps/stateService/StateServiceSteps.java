package steps.stateService;

import core.helper.Configure;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.StateServiceURL;

@Log4j2
public class StateServiceSteps extends Steps {

    public static String getErrorFromStateService(String orderId) {
        String traceback = null;
        try {
            traceback = new Http(StateServiceURL)
                    .get("/actions/?order_id={}", orderId)
                    .jsonPath().getString("list.findAll{it.status.contains('error')}.data.traceback");
        } catch (JsonPathException e) {
            log.error(e.toString());
        }
        if (StepsAspects.getCurrentStep().get() != null) {
            StepsAspects.getCurrentStep().get().addLinkItem(
                    new LinkItem("State service log", String.format("%s/actions/?order_id=%s", Configure.getAppProp("url.stateService"), orderId), "", LinkType.REPOSITORY));
        }
        return traceback;
    }

    @Step("Получение списка id из списка items")
    public static List<String> getOrdersIdList(String projectId) {
        return new Http(StateServiceURL)
                .get("/api/v1/projects/{}/items/", projectId)
                .assertStatus(200)
                .jsonPath()
                .getList("list.order_id");
    }

    @Step("Получение версии state service")
    public static Response getStateServiceVersion() {
         return new Http(StateServiceURL)
                .get("/version/")
                .assertStatus(200);
    }
}
