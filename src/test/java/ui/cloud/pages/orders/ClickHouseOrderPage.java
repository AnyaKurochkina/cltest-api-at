package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import static core.helper.StringUtils.$x;

@Getter
public class ClickHouseOrderPage extends NewOrderPage {

    private final Select group = Select.byLabel("Группы");
    private final Select group2 = Select.byLabel("Группы", 2);
    private final Select group3 = Select.byLabel("Группы", 3);
    private final Select group4 = Select.byLabel("Группы", 4);
    private final Input nameUser = Input.byLabel("Имя пользователя (админ с полными правами)");
    private final Input nameDB = Input.byLabel("Имя базы данных");
    private final SelenideElement generatePassButton1 = $x("(//input[@name='passwordGenerator'])[1]");
    private final SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");

    public ClickHouseOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }
}
