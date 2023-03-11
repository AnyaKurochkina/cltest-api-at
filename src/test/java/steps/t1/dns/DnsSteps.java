package steps.t1.dns;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.t1.dns.DnsZone;
import models.t1.dns.PowerDnsRrset;
import models.t1.dns.PowerDnsZone;
import models.t1.dns.Rrset;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.enums.Role.CLOUD_ADMIN;
import static core.helper.Configure.DNSService;
import static core.helper.Configure.PowerDns;

public class DnsSteps extends Steps {
    private static final String apiUrl = "/api/v1/";
    private static final String apiKey = "6e3bc9a0fa7eaebf282ad2e5";

    @Step("Создание zone")
    public static DnsZone createZone(JSONObject object, String projectId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post(apiUrl + "projects/{}/zones", projectId)
                .assertStatus(200)
                .extractAs(DnsZone.class);
    }

    @Step("Создание zone")
    public static Response createZoneResponse(JSONObject object, String projectId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post(apiUrl + "projects/{}/zones", projectId);
    }

    @Step("Получение zone по id={zoneId}")
    public static DnsZone getZoneById(String projectId, String zoneId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "projects/{}/zones/{}", projectId, zoneId)
                .assertStatus(200)
                .extractAs(DnsZone.class);
    }

    @Step("Создание Rrset")
    public static List<Rrset> createRrset(String projectId, String zoneId, JSONObject json) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .body(json)
                .post(apiUrl + "projects/{}/zones/{}/rrsets", projectId, zoneId)
                .assertStatus(200)
                .jsonPath()
                .getList("", Rrset.class);
    }

    @Step("Удаление zone")
    public static void deleteZone(String zoneId, String projectId) {
        new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .delete(apiUrl + "projects/{}/zones/{}", projectId, zoneId)
                .assertStatus(200);
    }

    @Step("Удаление Rrset")
    public static void deleteRrset(String projectId, String zoneId, String rrsetId) {
        new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .delete(apiUrl + "projects/{}/zones/{}/rrsets/{}", projectId, zoneId, rrsetId)
                .assertStatus(200);
    }

    @Step("Удаление zone из PowerDns")
    public static void deleteZoneFromPowerDns(String zoneId) {
        new Http(PowerDns)
                .setWithoutToken()
                .addHeader("X-Api-Key", apiKey)
                .delete(apiUrl + "servers/localhost/zones/{}", zoneId)
                .assertStatus(204);
    }

    @Step("Получение списка zone в PowerDns")
    public static List<PowerDnsZone> getZonePowerDnsList() {
        return new Http(PowerDns)
                .setWithoutToken()
                .addHeader("X-Api-Key", apiKey)
                .get(apiUrl + "servers/localhost/zones")
                .assertStatus(200)
                .jsonPath()
                .getList("", PowerDnsZone.class);
    }

    @Step("Получение zone в PowerDns по ZoneId")
    public static PowerDnsZone getZonePowerDnsById(String domainName) {
        return new Http(PowerDns)
                .setWithoutToken()
                .addHeader("X-Api-Key", apiKey)
                .get(apiUrl + "servers/localhost/zones/{}", domainName)
                .assertStatus(200)
                .extractAs(PowerDnsZone.class);
    }

    @Step("Проверка существования зоны в PowerDns")
    public static boolean isZoneExistInPowerDns(String domainName) {
        List<PowerDnsZone> list = getZonePowerDnsList();
        for (PowerDnsZone zone : list) {
            if (zone.getId().equals(domainName + ".")) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка существования записи в зоне PowerDns")
    public static boolean isRrsetExistInPowerDnsZone(String name, String domainName) {
        name = name + ".";
        List<PowerDnsRrset> rrsetList = getZonePowerDnsById(domainName).getRrsets();
        for (PowerDnsRrset rrset : rrsetList) {
            if (rrset.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Step("Получение списка зон")
    public static List<DnsZone> getZoneList(String projectId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "projects/{}/zones", projectId)
                .assertStatus(200)
                .jsonPath().getList("", DnsZone.class);
    }

    @Step("Получение списка Rrsets")
    public static List<Rrset> getRrsetList(String zoneId, String projectId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "projects/{}/zones/{}/rrsets", projectId, zoneId)
                .assertStatus(200)
                .jsonPath().getList("", Rrset.class);
    }

    @Step("Частичное обновление зоны")
    public static DnsZone partialUpdateZone(JSONObject json, String zoneId, String projectId) {
        return new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .body(json)
                .patch(apiUrl + "projects/{}/zones/{}", projectId, zoneId)
                .assertStatus(200)
                .extractAs(DnsZone.class);
    }

    @Step("Частичное обновление Rrset")
    public static void partialUpdateRrset(String projectId, String zoneId, String rrsetId, JSONObject json) {
        new Http(DNSService)
                .setRole(CLOUD_ADMIN)
                .body(json)
                .patch(apiUrl + "projects/{}/zones/{}/rrsets/{}", projectId, zoneId, rrsetId)
                .assertStatus(200);
    }

    @Step("Проверка существования зоны")
    public static boolean isZoneExist(String zoneId, String projectId) {
        List<DnsZone> list = getZoneList(projectId);
        for (DnsZone zone : list) {
            if (zone.getId().equals(zoneId)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка существования зоны по имени")
    public static boolean isZoneByNameExist(String name, String projectId) {
        List<DnsZone> list = getZoneList(projectId);
        for (DnsZone zone : list) {
            if (zone.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка существования записи")
    public static boolean isRrsetExist(String recordName, String zoneId, String projectId) {
        recordName = recordName + ".";
        List<Rrset> list = getRrsetList(zoneId, projectId);
        for (Rrset rrset : list) {
            if (rrset.getRecordName().equals(recordName)) {
                return true;
            }
        }
        return false;
    }

    @Step("Получение записи по имени")
    public static Rrset getRrsetByName(String recordName, String zoneId, String projectId) {
        recordName = recordName + ".";
        List<Rrset> list = getRrsetList(zoneId, projectId);
        for (Rrset rrset : list) {
            if (rrset.getRecordName().equals(recordName)) {
                return rrset;
            }
        }
        return null;
    }
}
