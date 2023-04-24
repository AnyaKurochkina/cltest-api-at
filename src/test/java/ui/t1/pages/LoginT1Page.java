package ui.t1.pages;

import core.enums.Role;
import ui.LoginPage;

public class LoginT1Page extends LoginPage {

    public LoginT1Page() {
        super();
    }

    public LoginT1Page(String project) {
        super(project);
    }

    public IndexPage signIn(Role role) {
        signInRole(role);
        return new IndexPage();
    }
}
