package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.productCatalog.product.GetProductList;
import models.productCatalog.product.Product;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ProductSteps extends Steps {
    @Step("Получение списка Продуктов продуктового каталога")
    public static List<Product> getProductList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Проверка сортировки списка продуктов")
    public static boolean isProductListSorted(List<Product> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            Product currentProduct = list.get(i);
            Product nextProduct = list.get(i + 1);
            Integer currentNumber = currentProduct.getNumber();
            Integer nextNumber = nextProduct.getNumber();
            String currentTitle = delNoDigOrLet(currentProduct.getTitle());
            String nextTitle = delNoDigOrLet(nextProduct.getTitle());
            if (currentNumber > nextNumber || ((currentNumber.equals(nextNumber)) && currentTitle.compareToIgnoreCase(nextTitle) > 0)) {
                return false;
            }
        }
        return true;
    }
}
