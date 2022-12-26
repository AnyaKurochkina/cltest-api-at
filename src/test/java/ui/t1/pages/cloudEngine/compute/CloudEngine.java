package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static core.helper.StringUtils.$x;

public class CloudEngine {
    SelenideElement btnConnect = $x("//button[.='Подключить']");
    SelenideElement connected = $x("//*[.='Сервис Cloud Compute подключен']");

    public void connectCloudCompute(){
        btnConnect.shouldBe(Condition.visible).click();
        connected.shouldBe(Condition.visible);
    }
}
