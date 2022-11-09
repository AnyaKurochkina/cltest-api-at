package ui.t1.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class IndexPage {
    final SelenideElement linkCloudEngine = $x("//a[.='T1 Cloud Engine']");
    final SelenideElement linkCloudDirector = $x("//a[.='Cloud Director']");

    @Step("Переход на страницу T1 Cloud Engine")
    public void goToCloudEngine() {
        linkCloudEngine.click();
    }

    @Step("Переход на страницу Cloud Director")
    public void goToCloudDirector() {
        linkCloudDirector.click();
    }
}