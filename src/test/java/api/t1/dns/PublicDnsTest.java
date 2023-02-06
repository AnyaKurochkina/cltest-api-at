package api.t1.dns;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.t1.dns.DnsZone;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static steps.t1.dns.DnsSteps.createPublicZone;

@Tag("dns")
@Epic("Днс сервис")
@Feature("zones")
public class PublicDnsTest extends Tests {
    @Test
    @TmsLink("")
    @DisplayName("Создание публичной зоны")
    public void createPublicZoneTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        JSONObject json = DnsZone.builder()
                .name("create_public_zone_test_api")
                .domainName("public.zone.test.api.ru")
                .type("public")
                .build().toJson();
        createPublicZone(json, project.getId());
    }

}
