package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static core.helper.StringUtils.$x;

@Getter
public class Dialog implements TypifiedElement {
    SelenideElement dialog;

    public Dialog(String title) {
        dialog = $x("//h2[.='{}']/ancestor::div[@role='dialog']", title);
    }

    public void setInputValue(String label, String value){
        SelenideElement element = dialog.$x(String.format("descendant::div[label[starts-with(.,'%s')]]/div/input", label));
        new Input(element).clear();
        new Input(element).setValue(value);
    }
    public void setDropDownValue(String label,String value){
        SelenideElement element = dialog.$x(String.format("descendant::div[label[starts-with(.,'%s')]]/div/input", label));
        DropDown dropDown = new DropDown(element);
        dropDown.select(value);
    }



}
