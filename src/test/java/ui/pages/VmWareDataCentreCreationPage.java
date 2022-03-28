package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.WebElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class VmWareDataCentreCreationPage implements Loadable {

    SelenideElement nameInput = $(By.xpath("//input[@id='root_name']"));
    SelenideElement pageTitle = $(By.xpath("//*[text()='Создание виртуального дата-центра VMware']"));
    SelenideElement wrongNameAlert = $(By.xpath("//*[text()='поле обязательно для заполнения']"));
    SelenideElement cpuInput = $(By.xpath("//*[text()='Виртуальный процессор (vCPU), Core']")).parent().parent();
    SelenideElement ramInput = $(By.xpath("//*[text()='Оперативная память (RAM), Gb']")).parent().parent();
    SelenideElement widthInput = $(By.xpath("//*[text()='Гарантированная ширина канала, Мбит/сек']")).parent().parent();
    SelenideElement ipv4Input = $(By.xpath("//*[text()='Публичные IPv4 адреса']")).parent().parent();
    SelenideElement confirmOrderBtn = $(By.xpath("//button[@data-testid='new-order-details-create']"));

    public VmWareDataCentreCreationPage() {
        checkPage();
    }

    @Override
    public void checkPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    public VmWareDataCentreCreationPage setNameDataCentre(String nameDataCentre) {
        nameInput.selectOption(5);
        nameInput.shouldBe(Condition.enabled).val(nameDataCentre);
        wrongNameAlert.shouldBe(Condition.disappear);
        return this;
    }

    public void confirmOrder() {
        confirmOrderBtn.shouldBe(Condition.enabled).click();
    }

    public VmWareDataCentreCreationPage setCpuValue(Integer cpuNumber) {
        pageTitle.scrollIntoView(true).shouldBe(Condition.enabled);
        cpuInput.$(By.xpath(".//input[@type='number']")).click();
        cpuInput.$(By.xpath(".//input[@type='number']"))
                .sendKeys(Keys.chord(Keys.BACK_SPACE), Keys.chord(Keys.BACK_SPACE), String.valueOf(cpuNumber));
        return this;
    }

    public VmWareDataCentreCreationPage setIpV4Value(Integer ipv4Value) {
        ipv4Input.$(By.xpath(".//input")).scrollIntoView(true).shouldBe(Condition.enabled).click();
        ipv4Input.$(By.xpath(".//input[@type='number']"))
                .sendKeys(Keys.chord(Keys.BACK_SPACE), String.valueOf(ipv4Value));
        return this;
    }

    public VmWareDataCentreCreationPage setRamValue(Integer ramValue) {
        pageTitle.scrollIntoView(true).shouldBe(Condition.enabled);
        ramInput.$(By.xpath(".//input")).shouldBe(Condition.enabled).click();
        ramInput.$(By.xpath(".//input[@type='number']"))
                .sendKeys(Keys.chord(Keys.BACK_SPACE), Keys.BACK_SPACE, String.valueOf(ramValue));
        return this;
    }

    public VmWareDataCentreCreationPage setWidthValue(Integer widthValue) {
        widthInput.$(By.xpath(".//input")).scrollIntoView(true).shouldBe(Condition.enabled).click();
        widthInput.$(By.xpath(".//input[@type='number']"))
                .sendKeys(Keys.chord(Keys.BACK_SPACE), Keys.chord(Keys.BACK_SPACE), Keys.chord(Keys.BACK_SPACE), String.valueOf(widthValue));
        return this;
    }

}
