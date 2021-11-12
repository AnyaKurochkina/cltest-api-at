package tests;

import core.CacheService;
import core.helper.Http;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.orderService.products.OpenShiftProject;
import models.orderService.products.WildFly;
import models.subModels.KafkaTopic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import steps.Steps;
import steps.accountManager.AccountSteps;
import steps.authorizer.AuthorizerSteps;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Epic("Финансы")
@Feature("Калькулятор")
@Tag("test")
public class CostTest extends Tests {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
    AccountSteps accountSteps = new AccountSteps();
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @SneakyThrows
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Списание средств за продукт {0}")
    @DisplayName("Списание средств за продукт")
    public void expenseAccount(OpenShiftProject resource) {
        //Todo: внести прожектЕнваерменты в прод шаре
        try (OpenShiftProject product = resource.createObjectExclusiveAccess()) {
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
            orderServiceSteps.changeProjectForOrder(product, projectTarget)
                    .assertStatus(200);
            Thread.sleep(5000);
            orderServiceSteps.changeProjectForOrder(product, projectSource)
                    .assertStatus(200);
        }
    }
}
