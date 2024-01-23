package ui.elements;

import com.codeborne.selenide.SelenideElement;
import org.intellij.lang.annotations.Language;

import static core.helper.StringUtils.$x;

public class MuiGridItem {
    @Language("XPath")
    static String xpath = "//div[(contains(@class, 'MuiBox-root') and text()= '{}') or contains(@class, 'MuiGrid-item') and .= '{}']";
    SelenideElement element;

    private MuiGridItem(SelenideElement element) {
        this.element = element;
    }

    public static MuiGridItem byText(String text){
        return new MuiGridItem($x(xpath, text, text));
    }

    public SelenideElement nextItem(){
        return element.$x("following::div");
    }
}
