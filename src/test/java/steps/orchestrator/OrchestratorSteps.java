package steps.orchestrator;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.orchestratorURL;

public class OrchestratorSteps extends Steps {

    public static Response checkPythonTemplate(PythonTemplate pythonTemplate) {
        return new Http(orchestratorURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject().put("data", pythonTemplate.getPythonData()).put("template", pythonTemplate.getPythonCode()))
                .post("/python_format");
    }

    public static Response checkPythonTemplate(JSONObject pythonTemplate) {
        return new Http(orchestratorURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(pythonTemplate)
                .post("/python_format");
    }
}
