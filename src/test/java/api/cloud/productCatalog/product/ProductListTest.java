package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.product.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductSteps.*;
import static steps.references.ReferencesStep.getPagesByName;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/products/",
            "productCatalog/products/createProduct.json");

    @DisplayName("Получение списка продуктов. Список отсортирован по number и title без учета спец. символов")
    @TmsLink("643387")
    @Test
    public void getProductListTest() {
        String productName = "create_product_example_for_get_list_test_api";
        Product.builder()
                .name(productName)
                .build()
                .createObject();
        List<Product> list = getProductList();
        assertTrue(isProductListSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка продуктов")
    @TmsLink("679088")
    @Test
    public void getMeta() {
        String str = getMetaProductList().getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка продуктов по фильтру is_open")
    @TmsLink("806303")
    @Test
    public void getProductListByIsOpen() {
        Product.builder()
                .name("get_products_by_status_test_api")
                .title("AtTestApiProduct")
                .build()
                .createObject();
        List<Product> productList = getProductListByFilter("is_open=true");
        productList.forEach(product -> assertTrue(product.getIsOpen()));
    }

    @DisplayName("Получение списка продуктов по фильтру category")
    @TmsLink("806304")
    @Test
    public void getProductListByCategory() {
        Product.builder()
                .name("get_products_by_category_test_api")
                .title("AtTestApiProduct")
                .category("vm")
                .build()
                .createObject();
        List<Product> productList = getProductListByFilter("category=vm");
        productList.forEach(product -> assertEquals("vm", product.getCategory()));
    }

    @DisplayName("Получение списка продуктов по фильтру in_general_list=true")
    @TmsLink("852654")
    @Test
    public void getProductListByInGeneralListTrue() {
        Product product = Product.builder()
                .name("get_products_by_in_general_list_true_test_api")
                .title("AtTestApiProduct")
                .inGeneralList(true)
                .build()
                .createObject();
        List<Product> productList = getProductListByFilter("in_general_list=true");
        assertTrue(productList.stream().anyMatch(product1 -> product1.getName().equals(product.getName())));
        productList.forEach(item -> assertTrue(item.getInGeneralList()));
    }


    @DisplayName("Получение списка продуктов по фильтру in_general_list=false")
    @TmsLink("852655")
    @Test
    public void getProductListByInGeneralListFalse() {
        Product product = Product.builder()
                .name("get_products_by_in_general_list_false_test_api")
                .title("AtTestApiProduct")
                .inGeneralList(false)
                .build()
                .createObject();
        List<Product> productList = getProductListByFilter("in_general_list=false");
        assertTrue(productList.stream().anyMatch(product1 -> product1.getName().equals(product.getName())));
        productList.forEach(item -> assertFalse(item.getInGeneralList()));
    }

    //todo Передалать тест в связи с изменениями.Убрали оргинфо и envs.
    @DisplayName("Получение списка продуктов по контексту id проекта")
    @Disabled
    @TmsLink("1039087")
    @Test
    public void getProductListWithProjectContext() {
//        Project project = Project.builder().build().createObject();
//        Response resp = ResourceManagerSteps.getProjectById(project.getId(), "project_environment");
//        String org = resp.jsonPath().getString("data.organization");
//        String infSys = resp.jsonPath().getString("data.information_system_id");
//        String envType = resp.jsonPath().getString("data.project_environment.environment_type").toLowerCase();
//        List<Product> list = getProductListByProjectContext(project.getId());
//        for (Product item : list) {
//            List<httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.ListItem> list1 = steps
//                    .getProductOrgInfoSystemById(item.getProductId()).getList();
//            assertTrue(steps.isOrgContains(list1, org));
//            for (httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.ListItem item1 : list1) {
//                List<String> informationSystems = item1.getInformationSystems();
//                assertTrue(informationSystems.contains(infSys) || informationSystems.isEmpty());
//            }
//            assertTrue(item.getEnvs().contains(envType));
//        }
    }

    @DisplayName("Получение списка категорий доступных по контексту id проекта")
    @TmsLink("1039088")
    @Test
    public void getCategoriesWithProjectContext() {
        Project project = Project.builder().build().createObject();
        List<String> actualList = steps.getAvailableCategoriesByContextProject(project.getId());
        Collection<String> categoriesList = getPagesByName("ProductCategoriesV2").jsonPath().getObject("[0].data", Map.class).values();
        assertTrue(categoriesList.containsAll(actualList));
    }

    @DisplayName("Получение списка категорий")
    @TmsLink("1039089")
    @Test
    public void getCategories() {
        List<String> actualList = steps.getAvailableCategories();
        Collection<String> categoriesList = getPagesByName("ProductCategoriesV2").jsonPath().getObject("[0].data", Map.class).values();
        assertTrue(categoriesList.containsAll(actualList));
    }

    @DisplayName("Получение списка продуктов отсортированного по статусу")
    @TmsLink("806312")
    @Test
    public void orderingByStatus() {
        List<Product> list = getProductListOrderingByStatus();
        assertTrue(isOrderingByStatus(list), "Список не отсортирован.");
    }
}
