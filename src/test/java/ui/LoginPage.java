package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Organization;
import ui.elements.TypifiedElement;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;
import static ui.elements.TypifiedElement.open;

public abstract class LoginPage {
    protected SelenideElement usernameInput = $x("//input[@id='username']");
    protected SelenideElement passwordInput = $x("//input[@id='password']");
    protected SelenideElement submitBtn = $x("//button[@type='submit']");

    public LoginPage(String project) {
        Organization org = Organization.builder().build().createObject();
        open(String.format("/?context=%s&type=project&org=%s", project, org.getName()));
        submitBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }

    public LoginPage() {
        open("");
    }

    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
        TypifiedElement.checkProject();
        final String theme = "\"light\"";
        final String key = "themeType";
        if(!Objects.equals(Selenide.sessionStorage().getItem(key), theme)) {
            Selenide.sessionStorage().setItem(key, theme);
            TypifiedElement.refresh();
        }
    }

    protected void signInRole(Role role) {
        GlobalUser user = GlobalUser.builder().role(role).build().createObject();
        signIn(Objects.requireNonNull(user.getUsername()), user.getPassword());
    }
}
