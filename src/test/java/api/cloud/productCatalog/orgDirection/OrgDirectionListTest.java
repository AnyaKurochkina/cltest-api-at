package api.cloud.productCatalog.orgDirection;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import models.cloud.productCatalog.service.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.OrgDirectionSteps.*;
import static steps.productCatalog.ProductCatalogSteps.isSorted;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionListTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    @DisplayName("Получение списка направлений")
    @TmsLink("643305")
    @Test
    public void getOrgDirectionListTest() {
        List<OrgDirection> list = getOrgDirectionList();
        assertTrue(isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка направлений")
    @TmsLink("679060")
    @Test
    public void getMeta() {
        String nextPage = getOrgDirectionsList().getMeta().getNext();
        String url = getAppProp("url.kong");
        if (!(nextPage == null)) {
            assertTrue(nextPage.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка сервисов использующих направление")
    @TmsLink("1287089")
    @Test
    public void getServiceListUserOrgDirectionTest() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("org_direction_used_in_service_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String name = "create_service_used_direction_test_api";
        Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .directionId(orgDirection.getId())
                .build()
                .createObject();
        String serviceName = getServiceUsedOrgDirection(orgDirection.getId()).jsonPath().getString("[0].name");
        assertEquals(name, serviceName);
    }
}
