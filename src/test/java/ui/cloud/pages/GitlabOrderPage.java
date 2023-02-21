package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.TextArea;

import java.util.UUID;

@Getter
public class GitlabOrderPage extends NewOrderPage {

    Input label = Input.byLabel("Метка");
    Input projectName = Input.byLabel("Название");
    DropDown role = DropDown.byLabel("Роль");
    TextArea participant = TextArea.byName("users");
    DropDown participant2 = DropDown.byLabel("Участники");

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public GitlabOrderPage() {
       label.setValue(labelValue);
    }

    public void checkOrderDetails(){
        if(getCalculationDetails().exists())
        {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
