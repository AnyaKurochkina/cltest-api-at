package ui.cloud.pages.productCatalog;

import lombok.Getter;
import ui.elements.Button;
import ui.elements.MultiSelect;
import ui.elements.SearchSelect;
import ui.elements.Select;

@Getter
public class RestrictionsPage {

    private final Button addContextRestrictionButton = Button
            .byXpath("//*[text()='Контекстные ограничения']/..//following-sibling::button");
    private final Select orgSelect = Select.byLabel("Организация");
    private final SearchSelect infSystemSelect = SearchSelect.byLabel("Информационная система");
    private final MultiSelect criticalitySelect = MultiSelect.byLabel("Критичность");
    private final MultiSelect envTypeSelect = MultiSelect.byLabel("Тип среды");
    private final MultiSelect envSelect = MultiSelect.byLabel("Среда");
}
