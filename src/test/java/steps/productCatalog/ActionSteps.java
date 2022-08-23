package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.productCatalog.action.Action;
import models.productCatalog.action.GetActionList;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ActionSteps extends Steps {

    @Step("Получение списка Действий продуктового каталога")
    public static List<Action> getActionList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/actions/")
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionList.class).getList();
    }

    @Step("Проверка сортировки списка действий")
    public static boolean isActionListSorted(List<Action> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            Action currentAction = list.get(i);
            Action nextAction = list.get(i + 1);
            Integer currentNumber = currentAction.getNumber();
            Integer nextNumber = nextAction.getNumber();
            String currentTitle = delNoDigOrLet(currentAction.getTitle());
            String nextTitle = delNoDigOrLet(nextAction.getTitle());
            if (currentNumber > nextNumber || ((currentNumber.equals(nextNumber)) && currentTitle.compareToIgnoreCase(nextTitle) > 0)) {
                return false;
            }
        }
        return true;
    }
}
