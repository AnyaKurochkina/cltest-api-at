package ui.cloud.pages.productCatalog;

import lombok.Getter;
import ui.elements.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class RestrictionsPage extends EntityPage {

    private final Button addContextRestrictionButton = Button
            .byXpath("//*[text()='Контекстные ограничения']/..//following-sibling::button");
    private final Button addButton = Button.byXpath("//div[@role='dialog']//button[.='Добавить']");
    private final Button saveButton = Button.byXpath("//div[@role='dialog']//button[.='Сохранить']");
    private final Select orgSelect = Select.byLabel("Организация");
    private final SearchSelect infSystemSelect = SearchSelect.byLabel("Информационная система");
    private final MultiSelect criticalCategorySelect = MultiSelect.byLabel("Критичность");
    private final MultiSelect envTypeSelect = MultiSelect.byLabel("Тип среды");
    private final MultiSelect envSelect = MultiSelect.byLabel("Среда");

    public RestrictionsPage checkContextRestrionsRecord(String organization, String infSystem, String criticalCategory,
                                                        String envType, String env) {
        Table.Row record = new Table("Критичность ИС").getRow(0);
        assertAll(
                () -> assertTrue(record.getValueByColumn("Организация").contains(organization)),
                () -> assertTrue(record.getValueByColumn("Информационная система").contains(infSystem)),
                () -> assertTrue(record.getValueByColumn("Критичность ИС").contains(criticalCategory)),
                () -> assertTrue(record.getValueByColumn("Тип среды").contains(envType)),
                () -> assertTrue(record.getValueByColumn("Среда").contains(env)));
        return this;
    }

    public RestrictionsPage deleteRestriction() {
        Table table = new Table("Критичность ИС");
        Menu.byElement(table.getRow(0).get().$x(".//button")).select("Удалить");
        new DeleteDialog().submitAndDelete();
        Alert.green("Контекстное ограничение удалено. Сохраните объект");
        return this;
    }

    public RestrictionsPage openEditDialog() {
        Table table = new Table("Критичность ИС");
        Menu.byElement(table.getRow(0).get().$x(".//button")).select("Редактировать");
        return this;
    }
}
