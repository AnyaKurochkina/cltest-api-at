package ui.productCatalog.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class LoginPage {
    private final static String BASE_URL = "http://d2puos-ap2006ln.corp.dev.vtb:5432";
    private final SelenideElement userField= $x("//*[@id='username']");
    private final SelenideElement passField = $x("//*[@id='password']");
    private final SelenideElement loginButton = $x("//*[@name = 'login']");

    public LoginPage() {
        Selenide.open(BASE_URL);
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
