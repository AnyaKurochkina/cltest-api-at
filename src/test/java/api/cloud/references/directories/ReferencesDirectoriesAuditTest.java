package api.cloud.references.directories;

import api.cloud.references.ReferencesBaseTest;
import core.enums.Role;
import core.helper.http.QueryBuilder;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.keyCloak.UserInfo;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.references.Directories;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.keyCloak.KeyCloakSteps.getUserInfo;
import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Directories")
public class ReferencesDirectoriesAuditTest extends ReferencesBaseTest {

    @DisplayName("Получение списка audit для определенного справочника")
    @TmsLink("SOUL-9178")
    @Test
    public void getDirectoryAuditListTest() {
        Directories directory = createDirectory(createDirectoryJson("get_audit_directory_test_api", DIRECTORIES_DESCRIPTION));
        List<ProductAudit> objectAuditList = getDirectoryAuditList(directory.getName());
        assertEquals(1, objectAuditList.size());
        ProductAudit productAudit = objectAuditList.get(0);
        assertAll(() -> assertEquals(directory.getId(), productAudit.getObjId()),
                () -> assertEquals("create", productAudit.getChangeType()));
    }

    @DisplayName("Получение деталей audit для справочника")
    @TmsLink("SOUL-9179")
    @Test
    public void getDirectoryAuditDetailsTest() {
        Directories directory = createDirectory(createDirectoryJson("get_audit_details_directory_test_api", DIRECTORIES_DESCRIPTION));
        List<ProductAudit> objectAuditList = getDirectoryAuditList(directory.getName());
        ProductAudit productAudit = objectAuditList.get(0);
        assertEquals(directory.getId(), productAudit.getObjId());
        Response response = getDirectoryAuditDetails(productAudit.getAuditId());
        Directories createdDirectory = response.jsonPath().getObject("new_value", Directories.class);
        JSONObject old_value = response.jsonPath().getObject("old_value", JSONObject.class);
        assertEquals(directory.getName(), createdDirectory.getName());
        assertTrue(old_value.isEmpty());
    }

    @DisplayName("Получение деталей audit для измененного справочника")
    @TmsLink("SOUL-9180")
    @Test
    public void getAuditDetailsChangedDirectoryTest() {
        Directories directory = createDirectory(createDirectoryJson("get_audit_details_changed_directory_test_api", DIRECTORIES_DESCRIPTION));
        String updatedDescription = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api";
        partialUpdatePrivateDirectoryByName(directory.getName(), new JSONObject().put("description", updatedDescription));
        List<ProductAudit> objectAuditList = getDirectoryAuditList(directory.getName());
        Response response = getDirectoryAuditDetails(objectAuditList.get(0).getAuditId());
        String newValueDescription = response.jsonPath().getString("new_value.description");
        String oldValueDescription = response.jsonPath().getString("old_value.description");
        assertEquals(directory.getDescription(), oldValueDescription);
        assertEquals(updatedDescription, newValueDescription);
    }

    @DisplayName("Получение списка audit для obj_key справочника")
    @TmsLink("SOUL-9181")
    @Test
    public void getAuditListWithObjKeyDirectoryTest() {
        Directories directory = createDirectory(createDirectoryJson("get_audit_obj_key_directory_test_api", DIRECTORIES_DESCRIPTION));
        deletePrivateDirectoryByName(directory.getName());
        createDirectory(directory.toJson());
        List<ProductAudit> auditListForObjKeys = getAuditListForDirectoryKeys(directory.getName());
        auditListForObjKeys.forEach(x -> assertEquals(x.getObjKeys().get("name"), directory.getName()));
    }

    @DisplayName("Получение информации о пользователе в списке Аудита справочника.")
    @TmsLink("SOUL-9182")
    @Test
    public void getAuditUserInfoWhoDidChangesDirectoryTest() {
        Directories directory = createDirectory(createDirectoryJson("get_user_info_audit_directory_test_api", DIRECTORIES_DESCRIPTION));
        List<ProductAudit> objectAuditList = getDirectoryAuditList(directory.getName());
        UserInfo userInfo = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        ProductAudit productAudit = objectAuditList.get(0);
        assertAll(
                () -> assertEquals(userInfo.getEmail(), productAudit.getUserEmail()),
                () -> assertEquals(userInfo.getGivenName(), productAudit.getUserFirstName()),
                () -> assertEquals(userInfo.getFamilyName(), productAudit.getUserLastName()));
    }

    @DisplayName("Получение списка audit по фильтру user__icontains")
    @TmsLink("SOUL-9183")
    @Test
    public void getDirectoryAuditWithFilterByUserEmailTest() {
        Directories directory = createDirectory(createDirectoryJson("get_user_info_audit_filtered_by_user_directory_test_api", DIRECTORIES_DESCRIPTION));
        String description = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api";

        partialUpdatePrivateDirectoryByName(directory.getName(), new JSONObject().put("description", description), Role.CLOUD_ADMIN);
        List<ProductAudit> objectAuditList = getDirectoryAuditList(directory.getName());
        assertEquals(2, objectAuditList.size());

        UserInfo userProductCatalogAdmin = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        UserInfo userCloudAdmin = getUserInfo(Role.CLOUD_ADMIN);

        List<ProductAudit> objectAuditListWithFilteredByFirstName = getDirectoryAuditListWithQuery(directory.getName(), new QueryBuilder().add("user__icontains", userProductCatalogAdmin.getGivenName()));
        assertEquals(1, objectAuditListWithFilteredByFirstName.size());
        ProductAudit productAudit = objectAuditListWithFilteredByFirstName.get(0);
        assertEquals(productAudit.getUserFirstName(), userProductCatalogAdmin.getGivenName());

        List<ProductAudit> objectAuditListWithFilteredByEmail = getDirectoryAuditListWithQuery(directory.getName(), new QueryBuilder().add("user__icontains", userCloudAdmin.getEmail()));
        assertEquals(1, objectAuditListWithFilteredByEmail.size());
        ProductAudit productAuditEmail = objectAuditListWithFilteredByEmail.get(0);
        assertEquals(productAuditEmail.getUserEmail(), userCloudAdmin.getEmail());

        List<ProductAudit> objectAuditListWithFilteredByLastName = getDirectoryAuditListWithQuery(directory.getName(), new QueryBuilder().add("user__icontains", userCloudAdmin.getFamilyName()));
        assertEquals(1, objectAuditListWithFilteredByLastName.size());
        ProductAudit productAuditLastName = objectAuditListWithFilteredByLastName.get(0);
        assertEquals(productAuditLastName.getUserLastName(), userCloudAdmin.getFamilyName());
    }
}
