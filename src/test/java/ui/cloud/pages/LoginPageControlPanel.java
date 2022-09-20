package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import models.authorizer.GlobalUser;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static core.helper.Configure.getAppProp;

public class LoginPageControlPanel {

    SelenideElement usernameInput = $x("//input[@id='username']");
    SelenideElement passwordInput = $x("//input[@id='password']");

    public LoginPageControlPanel() {
        open(getAppProp("url.control-panel"));
    }

    private IndexPage signIn(String user, String password){
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
        return new IndexPage();
    }

    public IndexPage signIn(Role role){
        GlobalUser user = GlobalUser.builder().role(role).build().createObject();
        return signIn(user.getUsername(), user.getPassword());
    }

}