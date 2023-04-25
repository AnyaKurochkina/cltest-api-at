package ui.cloud.pages;

import core.enums.Role;
import ui.LoginPage;

public class LoginCloudPage extends LoginPage {

    public LoginCloudPage() {
        super();
    }

    public LoginCloudPage(String project) {
        super(project);
    }

    public IndexPage signIn(Role role) {
        signInRole(role);
        return new IndexPage();
    }

}