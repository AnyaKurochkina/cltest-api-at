package ui.t1.pages;

import core.enums.Role;
import core.utils.Waiting;
import ui.LoginPage;
import ui.elements.TypifiedElement;

import java.time.Duration;

public class T1LoginPage extends LoginPage {

    public T1LoginPage(String project) {
        super(project);
    }

    public IndexPage signIn(Role role) {
        signInRole(role);
        return new IndexPage();
    }
}
