package api.cloud.productCatalog.template;

import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
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

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplatesListTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json");

    @DisplayName("Получение списка шаблонов")
    @TmsLink("643551")
    @Test
    public void getTemplateList() {
        List<ItemImpl> list = steps.getProductObjectList(GetTemplateListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка шаблонов")
    @TmsLink("682827")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetTemplateListResponse.class).getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }
}
