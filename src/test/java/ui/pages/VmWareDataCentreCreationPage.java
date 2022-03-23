package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.$;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class VmWareDataCentreCreationPage implements Loadable {

    SelenideElement nameInput = $(By.xpath("//input[@id='root_name']"));
    SelenideElement pageTitle = $(By.xpath("//*[text()='Создание виртуального дата-центра VMware']"));
    SelenideElement wrongNameAlert = $(By.xpath("//*[text()='поле обязательно для заполнения']"));
    SelenideElement cpuInput = $(By.xpath("//*[@class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-12'][.//*[text()='Виртуальный процессор (vCPU), Core']"));
    SelenideElement ramInput = $(By.xpath("//*[@class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-12'][.//*[text()='Оперативная память (RAM), Gb']"));
    SelenideElement widthInput = $(By.xpath("//*[contains(@class, 'MuiGrid-root MuiGrid-item MuiGrid-grid-xs')][.//*[text()='Гарантированная ширина канала, Мбит/сек']]"));
    SelenideElement confirmOrderBtn = $(By.xpath("//button[@data-testid='new-order-details-create']"));

    public VmWareDataCentreCreationPage() {
        checkPage();
    }

    @Override
    public void checkPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    public VmWareDataCentreCreationPage setNameDataCentre(String nameDataCentre){
        nameInput.shouldBe(Condition.enabled).val(nameDataCentre);
        wrongNameAlert.shouldBe(Condition.disappear);
        return this;
    }

    public void confirmOrder(){
        confirmOrderBtn.shouldBe(Condition.enabled).click();
    }

    public VmWareDataCentreCreationPage setCpuValue(Integer cpuNumber){
        cpuInput.$(By.xpath(".//input")).shouldBe(Condition.enabled).click();
        cpuInput.$(By.xpath(".//input[@type='number']")).sendKeys(Keys.BACK_SPACE);
        cpuInput.$(By.xpath(".//input[@type='number']")).sendKeys(Keys.BACK_SPACE);
        cpuInput.$(By.xpath(".//input[@type='number']")).sendKeys(String.valueOf(cpuNumber));
        return this;
    }

}
