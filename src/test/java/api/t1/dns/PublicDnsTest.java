package api.t1.dns;

import api.Tests;
import core.helper.DateValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.t1.dns.DnsZone;
import models.t1.dns.Rrset;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
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
        List<DnsZone> publicZoneList = getZoneList(projectId);
        publicZoneList.forEach(x -> {
            if (x.getName().contains("test_api")) {
                deleteZone(x.getId(), projectId);
            }
        });
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/Удаление публичной зоны")
    public void createPublicZoneTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        DnsZone zone = DnsZone.builder()
                .name("create_public_zone_test_api")
                .domainName(domainName)
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        assertTrue(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не создалась", zone.getName()));
        assertTrue(isZoneExistInPowerDns(zone.getDomainName()), String.format("Зона с именем %s не создалась в PowerDns", zone.getName()));
        deleteZone(dnsZone.getId(), projectId);
        assertFalse(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не удалена", zone.getName()));
        assertFalse(isZoneExistInPowerDns(zone.getDomainName()), String.format("Зона с именем %s не удалена в PowerDns", zone.getName()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Проверка что при создании публичной зоны, отображаются обязательные ресурсные записи")
    public void checkRrsetExistsWhenPublicZoneCreatedTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        DnsZone zone = DnsZone.builder()
                .name("check_rrset_exists_when_public_zone_created_test_api")
                .domainName(domainName)
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        List<Rrset> rrsets = dnsZone.getRrsets();
        assertTrue(rrsets.stream().anyMatch(x -> x.getRecordType().equals("NS")), "Ресурсная запись с типом NS отсутствует");
        assertTrue(rrsets.stream().anyMatch(x -> x.getRecordType().equals("SOA")), "Ресурсная запись с типом SOA отсутствует");
    }

    @Test
    @TmsLink("")
    @DisplayName("Удаление публичной зоны из PowerDns. Проверка обратной синхронизации.")
    public void deletePublicZoneFromPowerDns() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        DnsZone zone = DnsZone.builder()
                .name("delete_public_zone_from_power_dns_test_api")
                .domainName(domainName)
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        assertTrue(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не создалась", zone.getName()));
        assertTrue(isZoneExistInPowerDns(zone.getDomainName()), String.format("Зона с именем %s не создалась в PowerDns", zone.getName()));
        deleteZoneFromPowerDns(dnsZone.getDomainName());
        assertTrue(isZoneExist(dnsZone.getId(), projectId), String.format("Зона с именем %s не удалена", zone.getName()));
        assertFalse(isZoneExistInPowerDns(zone.getDomainName()), String.format("Зона с именем %s не удалена в PowerDns", zone.getName()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение публичной зоны по id")
    public void getPublicZoneByIdTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        DnsZone zone = DnsZone.builder()
                .name("get_public_zone_by_id_test_api")
                .domainName(domainName)
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
    @DisplayName("Получение списка зон")
    public void getPublicZoneListTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        String name = "public_zone_get_list_test_api";
        JSONObject json = DnsZone.builder()
                .name(name)
                .domainName(domainName)
                .type("public")
                .build()
                .toJson();
        createZone(json, projectId);
        List<DnsZone> zoneList = getZoneList(projectId);
        assertTrue(zoneList.stream().anyMatch(zone -> zone.getName().equals(name)));
        assertTrue(zoneList.size() > 0, "Длина списка долна быть больше 0");
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка rrset")
    public void getRrsetListTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        JSONObject json = DnsZone.builder()
                .name("get_rrset_list_public_zone_test_api")
                .domainName(domainName)
                .type("public")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        List<Rrset> rrsetList = getRrsetList(dnsZone.getId(), projectId);
        assertEquals(1, rrsetList.size());
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/удаление rrset")
    public void createRrsetTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        JSONObject json = DnsZone.builder()
                .name("create_rrset_public_zone_test_api")
                .domainName(domainName)
                .type("public")
                .build()
                .toJson();
        DnsZone dnsZone = createZone(json, projectId);
        String recordName = "generate." + domainName;
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
    @DisplayName("Частичное обновление rrset")
    public void partialUpdateRrsetTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        JSONObject json = DnsZone.builder()
                .name("partial_update_rrset_public_zone_test_api")
                .domainName(domainName)
                .type("public")
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

    @Test
    @TmsLink("")
    @DisplayName("Проверка формата даты создания и обновления")
    public void checkDateFormatPublicZoneTest() {
        String domainName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".ru";
        DnsZone zone = DnsZone.builder()
                .name("check_format_date_public_zone_test_api")
                .domainName(domainName)
                .type("public")
                .build();
        DnsZone dnsZone = createZone(zone.toJson(), projectId);
        DateValidator validator = new DateValidator(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"));
        assertTrue(validator.isValid(dnsZone.getCreated_at()), "Формат даты не соответствует yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        assertTrue(validator.isValid(dnsZone.getUpdated_at()), "Формат даты не соответствует yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
    }
}
