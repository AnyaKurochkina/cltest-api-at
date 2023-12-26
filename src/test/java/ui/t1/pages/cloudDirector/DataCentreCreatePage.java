package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.elements.*;
import ui.models.cloudDirector.StorageProfile;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static ui.cloud.pages.orders.OrderUtils.clickOrder;

public class DataCentreCreatePage {
    SelenideElement pageTitle = $(By.xpath("//*[text()='Создание виртуального дата-центра VMware']"));

    public DataCentreCreatePage() {
        pageTitle.shouldBe(Condition.visible);
    }

    public DataCentreCreatePage setDataCentreName(String name) {
        Input.byLabel("Имя виртуального дата-центра").setValue(name);
        return this;
    }

    public DataCentreCreatePage setCpu(String cpu) {
        Slider.byLabel("Виртуальный процессор (vCPU), Core").setValue(cpu);
        return this;
    }

    public DataCentreCreatePage setRam(String ram) {
        Slider.byLabel("Оперативная память (RAM), Gb").setValue(ram);
        return this;
    }

    public DataCentreCreatePage setDataCentreProfile(StorageProfile profile) {
        Select profileStorage = new Select(new Table($x("//table")).getRow(0).getElementByColumn("Профиль оборудования *"));
        if (!profileStorage.getValue().contains(profile.getName())) {
            profileStorage.setContains(profile.getName());
        }
        TextArea.byName("limit").setValue(profile.getLimit());
        return this;
    }

    public DataCentreCreatePage setRouterBandwidth(String mbs) {
        CheckBox.byId("root_configure_router").setChecked(true);
        Select.byLabel("Лимит пропускной способности канала, Мбит/сек").set(mbs);
        return this;
    }

    public VMwareOrganizationPage orderDataCentre() {
        clickOrder();
        return new VMwareOrganizationPage();
    }

    public void orderDataCentreWithSameName() {
        clickOrder("Имя виртуального дата-центра не уникально в организации");
    }
}
