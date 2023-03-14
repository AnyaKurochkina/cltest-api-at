package api.t1.dns;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.dns.DnsZone;
import models.t1.dns.Rrset;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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
 //       Project project = Project.builder().isForOrders(true).build().createObject();
        projectId = "proj-ls0vejlv7c";
    }

    @AfterAll
    public static void clearTestData() {
        List<DnsZone> publicZoneList = getZoneList(projectId);
        publicZoneList.forEach(x -> deleteZone(x.getId(), projectId));
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/Удаление приватной зоны")
    public void createPrivateZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("testapidddd.com")
                .domainName("testapidddd.com")
                .networks(Collections.singletonList("dba21584-7f19-4692-bd0c-06c1a49d75ee"))
                .type("private")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        assertTrue(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не создалась", zone.getName()));
        deleteZone(dnsZone.getId(), projectId);
        assertFalse(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не удалена", zone.getName()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление приватной зоны")
    public void partialUpdatePrivateZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("partial_update_private_zone_test_api")
                .domainName("partial.update.private.zone.test.api.ru")
                .networks(Collections.singletonList("dba21584-7f19-4692-bd0c-06c1a49d75ee"))
                .type("private")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        String description = "update description";
        DnsZone updatedZone = partialUpdateZone(new JSONObject().put("description", description), dnsZone.getId(), projectId);
        assertEquals(description, updatedZone.getDescription());
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка зон")
    public void getPublicZoneListTest() {
        JSONObject json = DnsZone.builder()
                .name("private_zone_get_list_test_api")
                .domainName("getlist.private.zone.test.api.ru")
                .networks(Collections.singletonList("dba21584-7f19-4692-bd0c-06c1a49d75ee"))
                .type("private")
                .build()
                .toJson();
        createZone(json, projectId);
        List<DnsZone> zoneList = getZoneList(projectId);
        assertTrue(zoneList.stream().anyMatch(zone -> zone.getName().equals("private_zone_get_list_test_api")));
        assertTrue(zoneList.size() > 0, "Длина списка долна быть больше 0");
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка rrset")
    public void getRrsetListTest() {
        JSONObject json = DnsZone.builder()
                .name("get_rrset_list_public_zone_test_api")
                .domainName("get.rrset.list.public.zone.test.api.ru")
                .type("private")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        List<Rrset> rrsetList = getRrsetList(dnsZone.getId(), projectId);
        assertEquals(2, rrsetList.size());
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/удаление rrset в private zone")
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
        assertTrue(isRrsetExistInPowerDnsZone(recordName, dnsZone.getDomainName()),
                String.format("Записи с name %s не существует в PowerDns", recordName));
        Rrset getRrset = Objects.requireNonNull(getRrsetByName(recordName, zoneId, projectId));
        deleteRrset(projectId, zoneId, getRrset.getId());
        assertFalse(isRrsetExist(recordName, zoneId, projectId),
                String.format("Запись с recordName %s существует", recordName));
        assertFalse(isRrsetExistInPowerDnsZone(recordName, dnsZone.getDomainName()),
                String.format("Запись с name %s существует в PowerDns", recordName));
    }

    @Test
    @TmsLink("")
    @DisplayName("Частичное обновление rrset в prvate zone")
    public void partialUpdateRrsetTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        JSONObject json = DnsZone.builder()
                .name("partial_update_rrset_private_zone_test_api")
                .domainName(domainName)
                .networks(Collections.singletonList("dba21584-7f19-4692-bd0c-06c1a49d75ee"))
                .type("private")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        String recordName = "partial.update." + domainName;
        Rrset rrset = Rrset.builder()
                .recordName(recordName)
                .recordType("A")
                .build();
        String zoneId = dnsZone.getId();
        createRrset(projectId, zoneId, rrset.toJson());
        Rrset createdRrset = getRrsetByName(recordName, zoneId, projectId);
        String updatedName = "r.update." + domainName;
        Objects.requireNonNull(createdRrset).setRecordName("r.update." + domainName);
        partialUpdateRrset(projectId, zoneId, Objects.requireNonNull(createdRrset).getId(), createdRrset.toJson());
        assertTrue(isRrsetExist(updatedName, zoneId, projectId));
    }
}
