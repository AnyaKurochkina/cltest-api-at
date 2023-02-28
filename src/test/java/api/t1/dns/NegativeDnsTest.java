package api.t1.dns;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.t1.dns.DnsZone;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.t1.dns.DnsSteps.*;

@Tag("dns")
@Epic("Днс сервис")
@Feature("zones")
public class NegativeDnsTest extends Tests {
    private static String projectId;

    public NegativeDnsTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        projectId = project.getId();
    }

    @AfterAll
    public static void clearTestData() {
        List<DnsZone> publicZoneList = getPublicZoneList(projectId);
        publicZoneList.forEach(x -> deleteZone(x.getId(), projectId));
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание зоны с уже существующим доменом.")
    public void createPublicZoneWithExistDomenTest() {
        DnsZone zone = DnsZone.builder()
                .name("exist_domen_public_zone_test_api")
                .domainName("public.zone.negative.test.api.ru")
                .type("public")
                .build();
        createZone(zone.toJson(), projectId);
        DnsZone zone2 = DnsZone.builder()
                .name("exist2_domen_public_zone_test_api")
                .domainName("public.zone.negative.test.api.ru")
                .type("public")
                .build();
        String errorMsg = createZoneResponse(zone2.toJson(), projectId).assertStatus(400)
                .jsonPath().getString("detail");
        assertEquals("Zone is already exist", errorMsg);
        assertFalse(isZoneExist(zone2.getName(), projectId), String.format("Зона с именем %s создалась", zone.getName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ru", "com", "."})
    @TmsLink("")
    @DisplayName("Создание зоны с запрещенными доменами")
    public void createRuDomenPublicZoneTest(String domain) {
        DnsZone zone = DnsZone.builder()
                .name("ru_domen_public_zone_test_api")
                .domainName(domain)
                .type("public")
                .build();
        String errorMsg = createZoneResponse(zone.toJson(), projectId).assertStatus(400)
                .jsonPath().getString("detail");
        assertEquals("Invalid zone", errorMsg);
        assertFalse(isZoneExist(zone.getName(), projectId), String.format("Зона с именем %s создалась", zone.getName()));
        assertFalse(isZoneExistInOpenDns(zone.getDomainName()), String.format("Зона с именем %s создалась в PowerDns", zone.getName()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Изменение домена")
    public void changeDomenPublicZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("change_domen_public_zone_test_api")
                .domainName("change.domen.public.zone.negative.test.api.ru")
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        partialUpdateZone(new JSONObject().put("domain_name", "change.domain.ru"), dnsZone.getId(), projectId);

    }

    @Test
    @TmsLink("")
    @DisplayName("Изменение типа зоны")
    public void changeTypePublicZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("change_type_public_zone_test_api")
                .domainName("change.type.public.zone.negative.test.api.ru")
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        partialUpdateZone(new JSONObject().put("type", "public"), dnsZone.getId(), projectId);
        //todo https://jira.t1-cloud.ru/jira-test/browse/PO-858 на анализе
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание зоны существующей в PowerDns")
    public void createExistInPowerDnsPublicZoneTest() {
        String name = getZoneOpenDnsList().get(0).getName();
        DnsZone zone = DnsZone.builder()
                .name("create_exist_in_power_dns_public_zone_test_api")
                .domainName(name)
                .type("public")
                .build();
        String errorMessage = createZoneResponse(zone.toJson(), projectId).assertStatus(400).jsonPath().getString("detail");
        assertEquals("Zone already exist", errorMessage);
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание приватной зоны без указания network")
    public void createPrivateZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("gg")
                .domainName("gg.ru")
                .type("private")
                .build();
        String actualErrorMessage = createZoneResponse(zone.toJson(), projectId).assertStatus(400).jsonPath().getString("detail");
        assertEquals("Cannot create private zone with null networks", actualErrorMessage);
    }

    //todo Проверить что мы не можем создать один и тот же домен из другого проекта. Если пубчлиные то ошибка, если приватный то все ок.
    //todo Ползователь создал example.com а другой создает test.example.com
    //todo У пользака есть права на проект он создает там зону, потом он идет в другой проект и создает test.example.com
    // todo у приватный зоны отсутсвует дефолтная запись типа NS
    //todo Создание public зоны когда private создана.
}
