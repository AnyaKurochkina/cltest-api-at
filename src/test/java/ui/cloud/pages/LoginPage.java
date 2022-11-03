package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Organization;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static core.helper.Configure.getAppProp;

public class LoginPage {

    SelenideElement usernameInput = $x("//input[@id='username']");
    SelenideElement passwordInput = $x("//input[@id='password']");
    SelenideElement submitBtn = $x("//button[@type='submit']");

    public LoginPage(String project) {
        Organization org = Organization.builder().build().createObject();
        open(String.format("/?context=%s&type=project&org=%s", project, org.getName()));
        submitBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }

    public LoginPage() {
        open(getAppProp("url.control-panel"));
    }


    private IndexPage signIn(String user, String password){
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
        TypifiedElement.checkProject();
        return new IndexPage();
    }

    public IndexPage signIn(Role role){
        GlobalUser user = GlobalUser.builder().role(role).build().createObject();
        return signIn(user.getUsername(), user.getPassword());
    }

}