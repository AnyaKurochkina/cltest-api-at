package ui.t1.pages;

import ui.elements.Button;
import ui.t1.pages.cloudEngine.compute.SshKeyList;

public class Profile {
    public SshKeyList getSshKeys(){
        Button.byText("SSH-ключи").click();
        return new SshKeyList();
    }
}
