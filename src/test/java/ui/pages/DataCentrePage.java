package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class DataCentrePage implements Loadable {

    private final SelenideElement titleOfDataCentrePage = $x("//*[text()='Информация о Виртуальном дата-центре']");
    private final SelenideElement actionsBtn = $x("//*[text()='Действия']");
    private final SelenideElement deleteDataCentreActionBtn = $x("//*[text()='Удалить']");
    private final SelenideElement idForDelete = $(By.cssSelector(".MuiTypography-colorTextSecondary>b"));
    private final SelenideElement idForDeleteInput = $(By.xpath("//input[@name='id']"));
    private final SelenideElement confirmDeleBtn = $(By.xpath("//*[text()='Удалить']"));

    @Override
    public void checkPage() {
        titleOfDataCentrePage.shouldBe(Condition.visible).shouldHave(Condition.text("Информация о Виртуальном дата-центре"));
    }

    public void deleteDataCentre(){
        actionsBtn.shouldBe(Condition.enabled).click();
        deleteDataCentreActionBtn.shouldBe(Condition.enabled).click();
        idForDeleteInput.shouldBe(Condition.visible).val(idForDelete.getText());
        confirmDeleBtn.shouldBe(Condition.enabled).click();
    }
}
