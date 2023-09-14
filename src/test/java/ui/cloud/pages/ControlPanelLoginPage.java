package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.Configure.getAppProp;
import static ui.elements.TypifiedElement.openPage;

public class ControlPanelLoginPage {

    SelenideElement usernameInput = $x("//input[@id='username']");
    SelenideElement passwordInput = $x("//input[@id='password']");

    public ControlPanelLoginPage() {
        openPage(getAppProp("url.control-panel"));
    }

    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
    }

    public void signIn(Role role) {
        GlobalUser user = GlobalUser.builder().role(role).build().createObject();
        signIn(user.getUsername(), user.getPassword());
    }

}