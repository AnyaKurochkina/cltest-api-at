package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;
import ui.cloud.tests.DropDown;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class Dialog {
    SelenideElement dialog;

    public Dialog(String title) {
        dialog = $x(String.format("//h2[.='%s']/ancestor::div[@role='dialog']", title));
    }

    public void setInputValue(String label, String value){
        SelenideElement element = dialog.$x(String.format("descendant::div[label[starts-with(.,'%s')]]/div/input", label));
        element.sendKeys(Keys.CONTROL + "A");
        element.sendKeys(Keys.BACK_SPACE);
        element.setValue(value);
    }

}
