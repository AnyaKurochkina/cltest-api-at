package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage implements Loadable {

    private final SelenideElement usernameInput = $(By.xpath("//input[@id='username']"));
    private final SelenideElement passwordInput = $(By.xpath("//input[@id='password']"));
    private final SelenideElement submitBtn = $(By.xpath("//button[@type='submit']"));

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

    @Override
    public void checkPage() {
        submitBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }
}
