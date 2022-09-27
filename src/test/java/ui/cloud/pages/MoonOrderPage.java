package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

@Getter
public class MoonOrderPage extends Product {

    //DropDown platform = DropDown.byLabel("Платформа");
    //DropDown osVersion = DropDown.byLabel("Версия ОС");
    //DropDown group = DropDown.byLabel("Группы");
    //DropDown roleServer = DropDown.byLabel("Роль сервера. (данное поле влияет на именование)");
    DropDown dataCentre = DropDown.byLabel("Дата-центр");
    DropDown segment = DropDown.byLabel("Сетевой сегмент");
    //DropDown configure = DropDown.byLabel("Конфигурация Core/RAM");
    DropDown cluster = DropDown.byLabel("Кластер OpenShift");
    Input countSession = Input.byLabel("Количество параллельных сессий");
    Input label = Input.byLabel("Метка");
    Input projectName = Input.byLabel("Имя проекта");

    String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    public MoonOrderPage() {
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
