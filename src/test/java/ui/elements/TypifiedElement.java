package ui.elements;

import com.codeborne.selenide.Selenide;

public interface TypifiedElement {
    String scrollCenter = "{block: 'center'}";

    static void refresh(){
        Selenide.refresh();
        new Alert().closeAll();
    }
}
