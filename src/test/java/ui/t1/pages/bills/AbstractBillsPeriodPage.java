package ui.t1.pages.bills;

import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.CheckBox;
import ui.elements.Select;

public class AbstractBillsPeriodPage<C extends AbstractBillsPeriodPage<?>> {

    private final Select organiztionSelect = Select.byLabel("Организация");
    private final Button exportButton = Button.byText("Выгрузить");
    private final CheckBox exportZeroPriceValuesCheckBox = CheckBox.byLabel("Выгружать нулевые значения стоимости");

    @SuppressWarnings("unchecked")
    @Step("Выбор организации с именем: {0}")
    public C chooseOrganization(String organizationName) {
        organiztionSelect.set(organizationName);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    @Step("Клик по кнопке 'Выгрузить'")
    public C clickExport() {
        exportButton.click();
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    @Step("Активация чекбокса 'Выгружать нулевые значения стоимости'")
    public C clickExportZeroPriceValuesCheckBox() {
        exportZeroPriceValuesCheckBox.setChecked(true);
        return (C) this;
    }
}
