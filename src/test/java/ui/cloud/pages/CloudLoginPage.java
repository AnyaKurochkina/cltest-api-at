package ui.cloud.pages;

import core.enums.Role;
import ui.LoginPage;

public class CloudLoginPage extends LoginPage {

    public CloudLoginPage(String project) {
        super(project);
    }

    public IndexPage signIn(Role role) {
        signInRole(role);
        return new IndexPage();
    }

}