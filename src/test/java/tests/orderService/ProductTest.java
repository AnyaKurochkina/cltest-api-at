package tests.orderService;

import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Project;
import models.orderService.products.Rhel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.accountManager.AccountSteps;
import steps.authorizer.AuthorizerSteps;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

@Log4j2
@Epic("Продукты")
@Feature("Действия над продуктами")
@Tags({@Tag("regress"), @Tag("prod")})
public class ProductTest extends Tests {
    AccountSteps accountSteps = new AccountSteps();
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @TmsLink("584453")
    @ParameterizedTest(name = "Перенос заказа. ВМ {0} при одинаковых префиксах")
    public void moveProductWithEqualsPrefix(Rhel resource) {
        try (Rhel product = resource.createObjectExclusiveAccess()) {
            Project projectSource = Project.builder().id(product.getProjectId()).build().createObject();
            String parentFolderId = authorizerSteps.getParentProject(product.getProjectId());
            Folder folderTarget = Folder.builder()
                    .title("folderForMoveProductWithEqualsPrefix")
                    .kind(Folder.DEFAULT)
                    .parentId(parentFolderId)
                    .build()
                    .createObjectPrivateAccess();
            Folder folderTarget2 = Folder.builder()
                    .title("folderForMoveProductWithEqualsPrefix2")
                    .kind(Folder.DEFAULT)
                    .parentId(parentFolderId)
                    .build()
                    .createObjectPrivateAccess();
            Project projectTarget = Project.builder().projectName("projectForMoveProductWithEqualsPrefix")
                    .prefix(projectSource.getPrefix())
                    .informationSystem(projectSource.getInformationSystem())
                    .projectEnvironment(projectSource.getProjectEnvironment())
                    .folderName(folderTarget.getName())
                    .build()
                    .createObjectPrivateAccess();
            Project projectTarget2 = Project.builder().projectName("projectForMoveProductWithEqualsPrefix2")
                    .prefix(projectSource.getPrefix())
                    .informationSystem(projectSource.getInformationSystem())
                    .projectEnvironment(projectSource.getProjectEnvironment())
                    .folderName(folderTarget2.getName())
                    .build()
                    .createObjectPrivateAccess();
            String accountFrom = accountSteps.getAccountIdByContext(parentFolderId);
            String accountTo = ((Account) Account.builder().folder(folderTarget).build().createObject()).getAccountId();
            accountSteps.transferMoney(accountFrom, accountTo, "1000.00", "Перевод в рамках тестирования");
            String accountTo2 = ((Account) Account.builder().folder(folderTarget2).build().createObject()).getAccountId();
            accountSteps.transferMoney(accountFrom, accountTo2, "1000.00", "Перевод в рамках тестирования");
            Waiting.sleep(60000);
            OrderServiceSteps.changeProjectForOrder(product, projectTarget);
            Waiting.sleep(60000);
            try {
                OrderServiceSteps.changeProjectForOrder(product, projectTarget2);
                Waiting.sleep(120000);

//          Заказ отсутствует в списке продуктов исходного проекта
                Assertions.assertFalse(OrderServiceSteps.getProductsWithStatus(projectTarget.getId(), "success").stream().anyMatch(id -> id.equals(product.getOrderId())),
                        "Заказ присутствует в списке продуктов исходного проекта");
                Float spent = accountSteps.getCurrentBalance(folderTarget.getName());
                Waiting.sleep(60000);

//            Расход в исходном проекте уменьшился на сумму перенесенного продукта
                Assertions.assertEquals(spent, accountSteps.getCurrentBalance(folderTarget.getName()), 0.01, "Сумма списания отличается от ожидаемой суммы");

//            Заказ отображается в списке продуктов целевого проекта
                Assertions.assertTrue(OrderServiceSteps.getProductsWithStatus(projectTarget2.getId(), "success").stream().anyMatch(id -> id.equals(product.getOrderId())),
                        "Заказ отсутствует в списке продуктов целевого проекта");
            } finally {
                OrderServiceSteps.changeProjectForOrder(product, projectSource);
            }
        }
    }

//    @Source(ProductArgumentsProvider.ONE_PRODUCT)
//    @ParameterizedTest(name = "Перенос заказа. ВМ {0} при одинаковых префиксах")
//    @DisplayName("Списание средств за продукт")
//    public void moveProductWithoutEqualsPrefix(Rhel resource) {
//        try (Rhel product = resource.createObjectExclusiveAccess()) {
//            Project projectSource = Project.builder().id(product.getProjectId()).build().createObject();
//            String parentFolderId = authorizerSteps.getParentProject(product.getProjectId());
//            Folder folderTarget = Folder.builder()
//                    .title("folder_for_account_expense")
//                    .kind(Folder.DEFAULT)
//                    .parentId(parentFolderId)
//                    .build()
//                    .createObject();
//            Project projectTarget = Project.builder().projectName("project_for_account_expense")
//                    .prefix(projectSource.getPrefix())
//                    .informationSystem(projectSource.getInformationSystem())
//                    .projectEnvironment(projectSource.getProjectEnvironment())
//                    .folderName(folderTarget.getName())
//                    .build()
//                    .createObject();
//            String accountFrom = accountSteps.getAccountIdByContext(parentFolderId);
//            String accountTo = ((Account) Account.builder().folder(folderTarget).build().createObject()).getAccountId();
//            accountSteps.transferMoney(accountFrom, accountTo, "1000.00", "Перевод в рамках тестирования");
//            OrderServiceSteps.changeProjectForOrder(product, projectTarget);
//            OrderServiceSteps.changeProjectForOrder(product, projectSource);
//        }
//    }

}
