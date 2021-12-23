package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Project;
import models.orderService.products.Rhel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.accountManager.AccountSteps;
import steps.authorizer.AuthorizerSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;

@Log4j2
@Epic("Продукты")
@Feature("Действия над продуктами")
@Tags({@Tag("regress"), @Tag("prod")})
public class ProductTest {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    AccountSteps accountSteps = new AccountSteps();
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Перенос заказа. ВМ {0} при одинаковых префиксах")
    @DisplayName("Списание средств за продукт")
    public void moveProductWithEqualsPrefix(Rhel resource) {
        try (Rhel product = resource.createObjectExclusiveAccess()) {
            Project projectSource = Project.builder().id(product.getProjectId()).build().createObject();
            String parentFolderId = authorizerSteps.getParentProject(product.getProjectId());
            Folder folderTarget = Folder.builder()
                    .title("folder_for_move_product")
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
            orderServiceSteps.changeProjectForOrder(product, projectTarget);

//            Заказ отсутствует в списке продуктов исходного проекта
            Assertions.assertFalse(orderServiceSteps.getProductsWithStatus(projectSource.getId(), "success").stream().anyMatch(id -> id.equals(product.getOrderId())),
                    "Заказ присутствует в списке продуктов исходного проекта");
//            Расход в исходном проекте уменьшился на сумму перенесенного продукта

//            Заказ отображается в списке продуктов целевого проекта
//            В истории действий продукта отображается действие Перенос в другой проект (кнопка просмотра схемы выполнения неактивна)

            orderServiceSteps.changeProjectForOrder(product, projectSource);
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
//            orderServiceSteps.changeProjectForOrder(product, projectTarget);
//            orderServiceSteps.changeProjectForOrder(product, projectSource);
//        }
//    }

}
