package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static core.helper.StringUtils.$$x;

public class Breadcrumb implements TypifiedElement {
    private final ElementsCollection breadcrumbItems = $$x("//a[contains(@class, 'BreadcrumbLabel')]");

    public static void click(String label) {
        Button.byElement(new Breadcrumb().getItem(label)).click();
    }

    public static void getItem(int index) {
        Button.byElement(new Breadcrumb().breadcrumbItems.get(index)).click();
    }

    @Step("Получение BreadcrumbItem по label {label}")
    private SelenideElement getItem(String label) {
        return breadcrumbItems.find(Condition.exactText(label));
    }

}
