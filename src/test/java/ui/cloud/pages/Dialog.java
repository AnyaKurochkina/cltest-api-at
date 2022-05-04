package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class Dialog {
    SelenideElement dialog;

    public Dialog(String title) {
        dialog = $x(String.format("//h2[.='%s']/ancestor::div[@role='dialog']", title));
    }

    public void setInputValue(String label, String value){
        dialog.$x(String.format("descendant::div[label[.='%s']]/div/input", label)).setValue(value);
    }

}
