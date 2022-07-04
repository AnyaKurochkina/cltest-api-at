package steps.stateService;

import core.helper.Configure;
import core.helper.http.Http;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import steps.Steps;

import static core.helper.Configure.StateServiceURL;

@Log4j2
public class StateServiceSteps extends Steps {

    public static String GetErrorFromStateService(String orderId) {
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
}
