package ui.productCatalog.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.Configure.getAppProp;

public class LoginPage {
    private final static String baseURL = getAppProp("base.url");
    private final SelenideElement userField= $x("//*[@id='username']");
    private final SelenideElement passField = $x("//*[@id='password']");
    private final SelenideElement loginButton = $x("//*[@name = 'login']");

    public LoginPage() {
        Selenide.open(baseURL);
    }

    /**
     * Вход по логину и паролю.
     * @param user имя пользователя
     * @param password пароль пользователя
     */
    public void login(String user, String password) {
        userField.setValue(user);
        passField.setValue(password);
        loginButton.click();
    }
}
