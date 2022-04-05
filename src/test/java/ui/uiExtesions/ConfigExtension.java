package ui.uiExtesions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ConfigExtension implements AfterEachCallback {
    //Основной конфиг Ui тестов находится в файле org.junit.TestsExecutionListener;

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        closeWebDriver();
    }
}
