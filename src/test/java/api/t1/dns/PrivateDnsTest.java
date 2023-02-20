package api.t1.dns;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.t1.dns.DnsZone;
import models.t1.dns.Rrset;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static steps.t1.dns.DnsSteps.*;

@Tag("dns")
@Epic("Днс сервис")
@Feature("zones")
public class PrivateDnsTest extends Tests {
    private static String projectId;

    public PrivateDnsTest() {
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
    @DisplayName("Создание/Удаление приватной зоны")
    public void createPrivateZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("gg")
                .domainName("gg.ru")
//                .networks(Arrays.asList("dba21584-7f19-4692-bd0c-06c1a49d75ee"))
                .type("private")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), "proj-ls0vejlv7c");
//        assertTrue(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не создалась", zone.getName()));
//        deleteZone(dnsZone.getId(), projectId);
//        assertFalse(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не удалена", zone.getName()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление приватной зоны")
    public void partialUpdatePrivateZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("partial_update_private_zone_test_api")
                .domainName("partial.update.private.zone.test.api.ru")
                .type("private")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        String description = "update description";
        DnsZone updatedZone = partialUpdateZone(new JSONObject().put("description", description), dnsZone.getId(), projectId);
        assertEquals(description, updatedZone.getDescription());
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка приватных зон")
    public void getPublicZoneListTest() {
        JSONObject json = DnsZone.builder()
                .name("private_zone_get_list_test_api")
                .domainName("getlist.private.zone.test.api.ru")
                .type("private")
                .build()
                .toJson();
        createZone(json, projectId);
        List<DnsZone> publicZoneList = getPublicZoneList(projectId);
        assertTrue(publicZoneList.size() > 0, "Длина списка долна быть больше 0");
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка rrset")
    public void getRrsetListTest() {
        JSONObject json = DnsZone.builder()
                .name("get_rrset_list_public_zone_test_api")
                .domainName("get.rrset.list.public.zone.test.api.ru")
                .type("public")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        List<Rrset> rrsetList = getRrsetList(dnsZone.getId(), projectId);
        assertEquals(2, rrsetList.size());
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/удаление rrset")
    public void createRrsetTest() {
        JSONObject json = DnsZone.builder()
                .name("createe_rrset_private_zone_test_api")
                .domainName("createe.rrset.private.zone.test.api.ru")
                .type("private")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        String recordName = "create_rrset_private";
        Rrset rrset = Rrset.builder()
                .recordName(recordName)
                .recordType("A")
                .build();
        String zoneId = dnsZone.getId();
        createRrset(projectId, zoneId, rrset.toJson());
        assertTrue(isRrsetExist(recordName, zoneId, projectId),
                String.format("Записи с recordName %s не существует", recordName));
        assertTrue(isRrsetExistInOpenDnsZone(recordName, dnsZone.getDomainName()),
                String.format("Записи с name %s не существует в PowerDns", recordName));
        Rrset getRrset = Objects.requireNonNull(getRrsetByName(recordName, zoneId, projectId));
        deleteRrset(projectId, zoneId, getRrset.getId());
        assertFalse(isRrsetExist(recordName, zoneId, projectId),
                String.format("Запись с recordName %s существует", recordName));
        assertFalse(isRrsetExistInOpenDnsZone(recordName, dnsZone.getDomainName()),
                String.format("Запись с name %s существует в PowerDns", recordName));
    }

    @Test
    @TmsLink("")
    @DisplayName("")
    public void partialUpdateRrsetTest() {
        JSONObject json = DnsZone.builder()
                .name("partial_update_rrset_private_zone_test_api")
                .domainName("partial_update.rrset.private.zone.test.api.ru")
                .type("private")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        String recordName = "update_rrset";
        Rrset rrset = Rrset.builder()
                .recordName(recordName)
                .recordType("A")
                .build();
        String zoneId = dnsZone.getId();
        createRrset(projectId, zoneId, rrset.toJson());
        Rrset createdRrset = getRrsetByName(recordName, zoneId, projectId);
        partialUpdateRrset(projectId, zoneId, Objects.requireNonNull(createdRrset).getId(), new JSONObject().put("record_name", "update_name"));
    }
}
