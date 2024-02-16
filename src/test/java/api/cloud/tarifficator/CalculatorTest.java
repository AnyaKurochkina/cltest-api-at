package api.cloud.tarifficator;

import api.Tests;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.accountManager.Account;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.NT;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.accountManager.AccountSteps;
import steps.authorizer.AuthorizerSteps;
import steps.calculator.CalculatorSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;

import java.util.Objects;

@Log4j2
@Epic("Финансы")
@Feature("Калькулятор")
@Tags({@Tag("regress"), @Tag("prod")})
public class CalculatorTest extends Tests {

    @TmsLink("456417")
    @SneakyThrows
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Списание средств за продукт {0}")
    public void expenseAccount(Astra resource) {
        try (Astra product = resource.createObjectExclusiveAccess()) {
            Project projectSource = Project.builder().id(product.getProjectId()).build().createObject();
            String parentFolderId = AuthorizerSteps.getParentProject(product.getProjectId());
            Folder folderTarget = Folder.builder()
                    .title("folder_for_account_expense")
                    .kind(Folder.DEFAULT)
                    .parentId(parentFolderId)
                    .build()
                    .createObject();
            Project projectTarget = Project.builder().projectName("project_for_account_expense")
//                    .prefix(projectSource.getPrefix())
                    .informationSystem(projectSource.getInformationSystem())
                    .projectEnvironmentPrefix(projectSource.getProjectEnvironmentPrefix())
                    .folderName(folderTarget.getName())
                    .build()
                    .createObject();
            String accountFromId = AccountSteps.getAccountIdByContext(parentFolderId);
            Account accountFrom = Account.builder().accountId(accountFromId).folderId(parentFolderId).build();
            String accountTo = ((Account) Account.builder().folder(folderTarget).build().createObject()).getAccountId();
            AccountSteps.transferMoney(accountFrom, accountTo, "1000.00", "Перевод в рамках тестирования");
            try {
                Float cost = CostSteps.getPreBillingTotalCost(product);
                while (cost < 0.01f)
                    cost += cost;
                Waiting.sleep(60000);
                OrderServiceSteps.changeProjectForOrder(product, projectTarget);
                Float spent = null;
                for (int i = 0; i < 15; i++) {
                    Waiting.sleep(20000);
                    spent = AccountSteps.getCurrentBalance(folderTarget.getName());
                    if (spent.equals(1000.0f))
                        continue;
                    break;
                }
                Assertions.assertEquals(cost, (1000.0f - Objects.requireNonNull(spent)), 0.01, "Сумма списания отличается от ожидаемой суммы");
            } catch (Throwable t) {
                try {
                    OrderServiceSteps.changeProjectForOrder(product, projectSource);
                } catch (Throwable t2) {
                    log.error(t2.toString());
                }
                throw t;
            }
            OrderServiceSteps.changeProjectForOrder(product, projectSource);
        }
    }

    @Test
    @SneakyThrows
    @Disabled
//    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    public void bigIntegrationCalculatorTest() {
        Organization organization = Organization.builder().type("default").build().createObject();
        String accountOrganizationId = AccountSteps.getAccountIdByContext(organization.getName());
        Account accountOrganization = Account.builder().accountId(accountOrganizationId).folderId(organization.getName()).build();
        Folder businessBlock = Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObjectPrivateAccess();
        Account accountBusinessBlock = Account.builder().parentId(accountOrganizationId).folder(businessBlock).build().createObjectPrivateAccess();
        Folder department = Folder.builder().parentId(businessBlock.getName()).kind(Folder.DEPARTMENT).build().createObjectPrivateAccess();
        Account accountDepartment = Account.builder().folder(department).parentId(accountBusinessBlock.getAccountId()).build().createObjectPrivateAccess();
        Folder folder = Folder.builder()
                .title("folder_for_big_integration_calculator_test")
                .kind(Folder.DEFAULT)
                .parentId(department.getName())
                .build()
                .createObjectPrivateAccess();
        Account accountFolder = Account.builder().folder(folder).parentId(accountDepartment.getAccountId()).build().createObjectPrivateAccess();

        AccountSteps.transferMoney(accountOrganization, accountFolder.getAccountId(), "1000.00", "Перевод в рамках тестирования");
        Project project = Project.builder().projectName("project_for_big_integration_calculator_test")
                .folderName(folder.getName())
                .build()
                .createObjectPrivateAccess();
        NT product = NT.builder().projectId(project.getId()).build().createObjectPrivateAccess();

        float spent = checkSpentAccount(product, folder.getName(), 1000.f);

        accountFolder.deleteObject();

        spent = checkSpentAccount(product, department.getName(), spent);
        String newAccount = CalculatorSteps.getCostOrderByOrderId(product.getOrderId()).getDetails().get(0).getAccountId();
        Assertions.assertEquals(accountDepartment.getAccountId(), newAccount, "Счет у продукта не изменился");

        System.out.println(1);
    }

    float checkSpentAccount(IProduct product, String folderId, Float currentBalance){
        Float cost = CostSteps.getPreBillingTotalCost(product);
        while (cost < 0.01f)
            cost += cost;
        Float spent = null;
        for (int i = 0; i < 15; i++) {
            Waiting.sleep(20000);
            spent = AccountSteps.getCurrentBalance(folderId);
            if (spent.equals(currentBalance))
                continue;
            break;
        }
        Assertions.assertEquals(cost, (currentBalance - Objects.requireNonNull(spent)), 0.01, "Сумма списания отличается от ожидаемой суммы");
        return spent;
    }



    @TmsLink("648902")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Сравнение стоимости продукта в статусе ON с ценой предбиллинга")
    public void costProductStatusOn(Astra resource) {
        try (Astra product = resource.createObjectExclusiveAccess()) {
            Waiting.sleep(60000);
            Float preBillingCostOn = CostSteps.getPreBillingCostPath(product, "items.find{it.type=='vm'}.resources_statuses.on.collect{it.total_price.toFloat()}.sum().toFloat()");
            Float currentCost = CostSteps.getCurrentCost(product);
            Assertions.assertEquals(preBillingCostOn, currentCost, 0.01f, "Стоимость предбиллинга не равна текущей стоимости");
        }
    }

    @Disabled
    @TmsLink("649012")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Сравнение стоимости продукта в статусе OFF с ценой предбиллинга")
    public void costProductStatusOff(Astra resource) {
        try (Astra product = resource.createObjectExclusiveAccess()) {
            Waiting.sleep(60000);
            Float preBillingCostOff = CostSteps.getPreBillingCostPath(product, "items.find{it.type=='vm'}.resources_statuses.off.collect{it.total_price.toFloat()}.sum().toFloat()");
            product.stopHard();
            Float currentCost = CostSteps.getCurrentCost(product);
            product.start();
            Assertions.assertEquals(preBillingCostOff, currentCost, 0.01f, "Стоимость предбиллинга не равна текущей стоимости");
        }
    }

}
