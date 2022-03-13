package stepsUi;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.driver;

public class Test {

    SelenideElement userNameInput = $(By.xpath("//input[@id='username']"));

    @org.junit.jupiter.api.Test
    public void test(){
        open("http://10.89.10.10:5432/management/");
        userNameInput.val("portal_admin");
        sleep(5000);
    }

    @AfterEach
    public void tierDown(){
        closeWebDriver();
    }
}
