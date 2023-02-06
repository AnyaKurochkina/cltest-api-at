package steps.t1.dns;

import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.dns.DnsZone;
import org.json.JSONObject;
import steps.Steps;

import static core.enums.Role.CLOUD_ADMIN;
import static core.helper.Configure.DNSService;

public class DnsSteps extends Steps {
    private static final String apiUrl = "/api/v1/";

    @Step("Создание public zone")
    public static DnsZone createPublicZone(JSONObject object, String projectId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post(apiUrl + "projects/{}/zones", projectId)
                .assertStatus(200)
                .extractAs(DnsZone.class);
    }
}
