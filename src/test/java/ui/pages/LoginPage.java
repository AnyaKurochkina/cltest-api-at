package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameInput = $(By.xpath("//input[@id='username']"));
    private final SelenideElement passwordInput = $(By.xpath("//input[@id='password']"));

    public void singIn(String username, String password){
        usernameInput.shouldBe(Condition.visible).val(username);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.shouldBe(Condition.visible).submit();
    }

    public void singIn(){
        usernameInput.shouldBe(Condition.visible).val("portal_admin");
        passwordInput.shouldBe(Condition.visible).val("portal_admin");
        passwordInput.shouldBe(Condition.visible).submit();
    }
}
