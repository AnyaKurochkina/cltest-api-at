package api.cloud.productCatalog.orgDirection;

import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void getOrgDirectionList() {
        List<ItemImpl> list = steps.getProductObjectList(GetOrgDirectionListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка направлений")
    @TmsLink("679060")
    @Test
    public void getMeta() {
        String nextPage = steps.getMeta(GetOrgDirectionListResponse.class).getNext();
        String url = getAppProp("url.kong");
        if (!(nextPage == null)) {
            assertTrue(nextPage.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка сервисов использующих направление")
    @TmsLink("")
    @Test
    public void getServiceListUserOrgDirectionTest() {

    }
}
