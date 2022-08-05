package tests.productCatalog.product;

import core.helper.Configure;
import core.helper.http.Response;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.product.getProducts.getProductsExportList.ExportItem;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.product.getProducts.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.Project;
import models.productCatalog.product.Categories;
import models.productCatalog.product.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import steps.resourceManager.ResourceManagerSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductSteps.getProductList;
import static steps.productCatalog.ProductSteps.isProductListSorted;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/products/",
            "productCatalog/products/createProduct.json");

    @DisplayName("Получение списка продуктов")
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
        String str = steps.getMeta(GetProductsResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
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
        List<ItemImpl> productList = steps.getProductObjectList(GetProductsResponse.class, "?is_open=true");
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getIsOpen());
        }
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
        List<ItemImpl> productList = steps.getProductObjectList(GetProductsResponse.class, "?category=vm");
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertEquals("vm", listItem.getCategory());
        }
    }

    @DisplayName("Получение списка продуктов по фильтру in_general_list=true")
    @TmsLink("852654")
    @Test
    public void getProductListByInGeneralListTrue() {
        String name = "get_products_by_in_general_list_true_test_api";
        Product.builder()
                .name(name)
                .title("AtTestApiProduct")
                .inGeneralList(true)
                .build()
                .createObject();
        List<ItemImpl> productList = steps.getProductObjectList(GetProductsResponse.class, "?in_general_list=true");
        assertTrue(steps.isContains(productList, name));
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getInGeneralList());
        }
    }

    @DisplayName("Получение списка продуктов по фильтру in_general_list=false")
    @TmsLink("852655")
    @Test
    public void getProductListByInGeneralListFalse() {
        String name = "get_products_by_in_general_list_false_test_api";
        Product.builder()
                .name(name)
                .title("AtTestApiProduct")
                .inGeneralList(false)
                .build()
                .createObject();
        List<ItemImpl> productList = steps.getProductObjectList(GetProductsResponse.class, "?in_general_list=false");
        assertTrue(steps.isContains(productList, name));
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertFalse(listItem.getInGeneralList());
        }
    }
    //todo Убрать хардкод проекта логику прохождения по списку убрать.
    @DisplayName("Получение списка продуктов по контексту id проекта")
    @TmsLink("1039087")
    @Test
    public void getProductListWithProjectContext() {
        Project project = Project.builder().build().createObject();
        Response resp = ResourceManagerSteps.getProjectById(project.getId(), "project_environment");
        String org = resp.jsonPath().getString("data.organization");
        String infSys = resp.jsonPath().getString("data.information_system_id");
        String envType = resp.jsonPath().getString("data.project_environment.environment_type").toLowerCase();
        List<ListItem> list = steps.getProductListByProjectContext(project.getId());
        for (ListItem item : list) {
            List<httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.ListItem> list1 = steps
                    .getProductOrgInfoSystemById(item.getId()).getList();
            assertTrue(steps.isOrgContains(list1, org));
            for (httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.ListItem item1 : list1) {
                List<String> informationSystems = item1.getInformationSystems();
                assertTrue(informationSystems.contains(infSys) || informationSystems.isEmpty());
            }
            assertTrue(item.getEnvs().contains(envType));
        }
    }
    @DisplayName("Получение списка категорий доступных по контексту id проекта")
    @TmsLink("1039088")
    @Test
    public void getCategoriesWithProjectContext() {
        Project project = Project.builder().build().createObject();
        List<String> actualList = steps.getAvailableCategoriesByContextProject(project.getId());
        List<String> categoriesList = Categories.getCategoriesList();
        assertEquals(categoriesList, actualList);
    }

    @DisplayName("Получение списка категорий")
    @TmsLink("1039089")
    @Test
    public void getCategories() {
        List<String> actualList = steps.getAvailableCategories();
        List<String> categoriesList = Categories.getCategoriesList();
        assertEquals(categoriesList, actualList);
    }

    @DisplayName("Получение списка products export")
    @TmsLink("1061110")
    @Test
    public void getProductExportList() {
        List<ExportItem> productsExportList = steps.getProductsExportList();
        for (ExportItem item : productsExportList) {
          assertNotNull(item.getOrgInfoSystems());
        }
    }

    @DisplayName("Получение списка products export в форматах xml/csv/json")
    @TmsLink("1081759")
    @Test
    public void getProductExportListXml() {
        String xml = "xml";
        assertEquals(xml, steps.getProductsExportListInFormat(xml).getContentType());
        String csv = "csv";
        assertEquals(csv, steps.getProductsExportListInFormat(csv).getContentType());
        String json = "json";
        assertEquals(json, steps.getProductsExportListInFormat(json).getContentType());
    }
}
