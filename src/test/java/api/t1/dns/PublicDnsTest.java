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
public class PublicDnsTest extends Tests {
    private static String projectId;

    public PublicDnsTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        projectId = project.getId();
    }

    @AfterAll
    public static void clearTestData() {
        List<DnsZone> publicZoneList = getPublicZoneList(projectId);
        publicZoneList.forEach(x -> {
            if (x.getType().equals("public")) {
                deleteZone(x.getId(), projectId);
            }
        });
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/Удаление публичной зоны")
    public void createPublicZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("create_public_zone_test_api")
                .domainName("public.zone.test.api.ru")
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        assertTrue(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не создалась", zone.getName()));
        assertTrue(isZoneExistInOpenDns(zone.getDomainName()), String.format("Зона с именем %s не создалась в PowerDns", zone.getName()));
        deleteZone(dnsZone.getId(), projectId);
        assertFalse(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не удалена", zone.getName()));
        assertFalse(isZoneExistInOpenDns(zone.getDomainName()), String.format("Зона с именем %s не удалена в PowerDns", zone.getName()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение публичной зоны по id")
    public void getPublicZoneByIdTest() {
        DnsZone zone = DnsZone.builder()
                .name("get_public_zone_by_id_test_api")
                .domainName("getpubliczonebyid.test.api.ru")
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        DnsZone getZone = getZoneById(projectId, dnsZone.getId());
        assertEquals(dnsZone, getZone);
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление публичной зоны")
    public void partialUpdatePublicZoneTest() {
        DnsZone zone = DnsZone.builder()
                .name("partial_update_public_zone_test_api")
                .domainName("partial.update.public.zone.test.api.ru")
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        String description = "update description";
        DnsZone updatedZone = partialUpdateZone(new JSONObject().put("description", description), dnsZone.getId(), projectId);
        assertEquals(description, updatedZone.getDescription());
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка публичных зон")
    public void getPublicZoneListTest() {
        JSONObject json = DnsZone.builder()
                .name("public_zone_get_list_test_api")
                .domainName("getlist.public.zone.test.api.ru")
                .type("public")
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
                .name("create_rrset_public_zone_test_api")
                .domainName("create.rrset.public.zone.test.api.ru")
                .type("public")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        String recordName = "create_rrset";
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
                .name("partial_update_rrset_public_zone_test_api")
                .domainName("partial_update.rrset.public.zone.test.api.ru")
                .type("public")
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
