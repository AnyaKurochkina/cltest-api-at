package ui.elements;

import com.codeborne.selenide.Selenide;

public interface TypifiedElement {
    String scrollCenter = "{block: 'center'}";

    static void refresh(){
        Selenide.refresh();
        new Alert().close();
    }

    static void open(String url){
        Selenide.open(url);
        new Alert().close();
    }
}
