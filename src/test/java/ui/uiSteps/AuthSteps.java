package ui.uiSteps;

import io.qameta.allure.Step;
import ui.pages.LoginPage;

public class AuthSteps {

    @Step
    public void signIn(){
        //Логинимся
        LoginPage loginPage = new LoginPage();
        loginPage.singIn();
    }

    @Step
    public void signIn(String login, String password){
        //Логинимся
        LoginPage loginPage = new LoginPage();
        loginPage.singIn(login, password);
    }
}
