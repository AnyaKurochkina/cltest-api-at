package tests.tarifficator;

import core.helper.JsonHelper;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Project;
import models.orderService.products.Rhel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import steps.accountManager.AccountSteps;
import steps.authorizer.AuthorizerSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;
import tests.Tests;

import java.util.Map;
import java.util.Objects;

@Log4j2
@Epic("Финансы")
@Feature("Калькулятор")
@Tags({@Tag("regress"), @Tag("prod")})
public class CalculatorTest extends Tests {
    final OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    final AccountSteps accountSteps = new AccountSteps();
    final AuthorizerSteps authorizerSteps = new AuthorizerSteps();
    final CostSteps costSteps = new CostSteps();

    @TmsLink("456417")
    @SneakyThrows
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Списание средств за продукт {0}")
    public void expenseAccount(Rhel resource) {
        try (Rhel product = resource.createObjectExclusiveAccess()) {
            Project projectSource = Project.builder().id(product.getProjectId()).build().createObject();
            String parentFolderId = authorizerSteps.getParentProject(product.getProjectId());
            Folder folderTarget = Folder.builder()
                    .title("folder_for_account_expense")
                    .kind(Folder.DEFAULT)
                    .parentId(parentFolderId)
                    .build()
                    .createObject();
            Project projectTarget = Project.builder().projectName("project_for_account_expense")
                    .prefix(projectSource.getPrefix())
                    .informationSystem(projectSource.getInformationSystem())
                    .projectEnvironment(projectSource.getProjectEnvironment())
                    .folderName(folderTarget.getName())
                    .build()
                    .createObject();
            String accountFrom = accountSteps.getAccountIdByContext(parentFolderId);
            String accountTo = ((Account) Account.builder().folder(folderTarget).build().createObject()).getAccountId();
            accountSteps.transferMoney(accountFrom, accountTo, "1000.00", "Перевод в рамках тестирования");
            try {
                Float cost = costSteps.getPreBillingTotalCost(product);
                while (cost < 0.01f)
                    cost += cost;
                Waiting.sleep(60000);
                orderServiceSteps.changeProjectForOrder(product, projectTarget);
                Float spent = null;
                for (int i = 0; i < 15; i++) {
                    Waiting.sleep(20000);
                    spent = accountSteps.getCurrentBalance(folderTarget.getName());
                    if (spent.equals(1000.0f))
                        continue;
                    break;
                }
                Assertions.assertEquals(cost, (1000.0f - Objects.requireNonNull(spent)), 0.01, "Сумма списания отличается от ожидаемой суммы");
            } catch (Throwable t) {
                try {
                    orderServiceSteps.changeProjectForOrder(product, projectSource);
                } catch (Throwable t2) {
                    log.error(t2.toString());
                }
                throw t;
            }
            orderServiceSteps.changeProjectForOrder(product, projectSource);
        }
    }


    @TmsLink("648902")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Сравнение стоимости продукта в статусе ON с ценой предбиллинга")
    public void costProductStatusOn(Rhel resource) {
        try (Rhel product = resource.createObjectExclusiveAccess()) {
            Waiting.sleep(60000);
            Float preBillingCostOn = costSteps.getPreBillingCostPath(product, "items.find{it.type=='vm'}.resources_statuses.on.collect{it.total_price.toFloat()}.sum().toFloat()");
            Float currentCost = costSteps.getCurrentCost(product);
            Assertions.assertEquals(preBillingCostOn, currentCost, 0.01f, "Стоимость предбиллинга не равна текущей стоимости");
        }
    }

    @TmsLink("649012")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Сравнение стоимости продукта в статусе OFF с ценой предбиллинга")
    public void costProductStatusOff(Rhel resource) {
        try (Rhel product = resource.createObjectExclusiveAccess()) {
            Waiting.sleep(60000);
            Float preBillingCostOff = costSteps.getPreBillingCostPath(product, "items.find{it.type=='vm'}.resources_statuses.off.collect{it.total_price.toFloat()}.sum().toFloat()");
            product.stopHard();
            Float currentCost = costSteps.getCurrentCost(product);
            product.start();
            Assertions.assertEquals(preBillingCostOff, currentCost, 0.01f, "Стоимость предбиллинга не равна текущей стоимости");
        }
    }

}
