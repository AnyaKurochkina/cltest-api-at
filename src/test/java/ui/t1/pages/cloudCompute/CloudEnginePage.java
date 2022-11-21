package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static core.helper.StringUtils.$x;

public class CloudEnginePage {
    SelenideElement btnConnect = $x("//button[.='Подключить']");
    SelenideElement connected = $x("//*[.='Сервис Cloud Compute подключен']");

    public void connectCloudCompute(){
        btnConnect.shouldBe(Condition.visible).click();
        connected.shouldBe(Condition.visible);
    }
}
