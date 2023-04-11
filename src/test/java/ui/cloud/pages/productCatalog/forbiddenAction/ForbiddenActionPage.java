package ui.cloud.pages.productCatalog.forbiddenAction;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;

import static core.helper.StringUtils.$x;

public class ForbiddenActionPage extends BasePage {

    public ForbiddenActionPage() {
        $x("//a[text()='Запрещенные действия']").shouldBe(Condition.visible);
    }

    @Step("Удаление запрещенного действия")
    public ForbiddenActionsListPage delete() {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
        return new ForbiddenActionsListPage();
    }
}
