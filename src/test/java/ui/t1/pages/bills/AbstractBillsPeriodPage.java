package ui.t1.pages.bills;

import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.CheckBox;
import ui.elements.Select;

public class AbstractBillsPeriodPage {

    private final Select organiztionSelect = Select.byLabel("Организация");
    private final Button exportButton = Button.byText("Выгрузить");
    private final CheckBox exportZeroPriceValuesCheckBox = CheckBox.byLabel("Выгружать нулевые значения стоимости");

    @Step("Выбор организации с именем: {0}")
    public AbstractBillsPeriodPage chooseOrganization(String organizationName) {
        organiztionSelect.set(organizationName);
        return this;
    }

    @Step("Клик по кнопке 'Выгрузить'")
    public AbstractBillsPeriodPage clickExport() {
        exportButton.click();
        return this;
    }

    @Step("Активация чекбокса 'Выгружать нулевые значения стоимости'")
    public AbstractBillsPeriodPage clickExportZeroPriceValuesCheckBox() {
        exportZeroPriceValuesCheckBox.setChecked(true);
        return this;
    }
}
