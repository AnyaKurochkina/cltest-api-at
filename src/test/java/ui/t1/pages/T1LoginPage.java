package ui.t1.pages;

import core.enums.Role;
import ui.LoginPage;

public class T1LoginPage extends LoginPage {

    public T1LoginPage() {
        super();
    }

    public T1LoginPage(String project) {
        super(project);
    }

    public IndexPage signIn(Role role) {
        signInRole(role);
        return new IndexPage();
    }
}
