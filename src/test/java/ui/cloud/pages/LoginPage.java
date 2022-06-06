package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.enums.Role;
import models.authorizer.GlobalUser;
import models.authorizer.Organization;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    SelenideElement usernameInput = $x("//input[@id='username']");
    SelenideElement passwordInput = $x("//input[@id='password']");
    SelenideElement submitBtn = $x("//button[@type='submit']");

    public LoginPage(String project) {
        Organization org = Organization.builder().build().createObject();
        open("https://prod-portal-front.cloud.vtb.ru/vm/orders/1f94dabf-37f7-4050-9a4b-e3306264d1ff/main?context=proj-frybyv41jh&type=project&org=vtb");//(String.format("/?context=%s&type=project&org=%s", project, org.getName()));
        submitBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }

    public LoginPage() {
        open("");
    }

    public IndexPage singIn(String user, String password){
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
        return new IndexPage();
    }

    public IndexPage singIn(){
        GlobalUser user = GlobalUser.builder().role(Role.ADMIN).build().createObject();
        return singIn(user.getUsername(), user.getPassword());
    }

}