package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.io.File;

import static com.codeborne.selenide.Selenide.$x;

public class InputFile {
    private final SelenideElement importFile = $x("//*[@id = 'attachment-input']");
    private final SelenideElement submit = $x("//*[text() = 'Импорт']");
    private final String path;

    public InputFile(String path) {
        this.path = path;

    }

    public void importFile() {
        importFile.uploadFile(new File(path));
        submit.shouldBe(Condition.enabled).click();
    }
}
