package ui.elements;

import com.codeborne.selenide.SelenideElement;

import java.io.File;

import static com.codeborne.selenide.Selenide.$x;

public class InputFile {
    private final SelenideElement fileInput = $x("//input[@type='file']");
    private final Button importButton = Button.byText("Импорт");
    private final String path;

    public InputFile(String path) {
        this.path = path;
    }

    public void importFileAndSubmit() {
        fileInput.uploadFile(new File(path));
        importButton.click();
    }

    public void importFile() {
        fileInput.uploadFile(new File(path));
    }
}
