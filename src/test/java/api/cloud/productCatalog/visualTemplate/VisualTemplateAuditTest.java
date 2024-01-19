package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import core.enums.Role;
import core.helper.StringUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.keyCloak.UserInfo;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.keyCloak.KeyCloakSteps.getUserInfo;
import static steps.productCatalog.ProductCatalogSteps.getObjectAuditList;
import static steps.productCatalog.VisualTemplateSteps.createVisualTemplate;
import static steps.productCatalog.VisualTemplateSteps.partialUpdateVisualTemplate;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateAuditTest extends Tests {

    @DisplayName("Получение информации о пользователе в списке Аудита для шаблона отображения.")
    @TmsLink("SOUL-8886")
    @Test
    public void getAuditUserInfoWhoDidChangesTest() {
        String visualTemplateName = "get_audit_user_visual_template_test_api";
        ItemVisualTemplate visualTemplate = createVisualTemplate(visualTemplateName);
        partialUpdateVisualTemplate(visualTemplate.getId(), new JSONObject().put("name", StringUtils.getRandomStringApi(7)));
        partialUpdateVisualTemplate(visualTemplate.getId(), new JSONObject().put("title", StringUtils.getRandomStringApi(7)));
        List<ProductAudit> objectAuditList = getObjectAuditList("item_visual_templates", visualTemplate.getId());
        UserInfo userInfo = getUserInfo(Role.PRODUCT_CATALOG_ADMIN);
        for (ProductAudit productAudit : objectAuditList) {
            assertEquals(userInfo.getEmail(), productAudit.getUserEmail());
            assertEquals(userInfo.getGivenName(), productAudit.getUserFirstName());
            assertEquals(userInfo.getFamilyName(), productAudit.getUserLastName());
        }
    }
}
