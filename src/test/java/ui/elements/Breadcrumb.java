package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.List;

import static core.helper.StringUtils.$$x;

/**
 * Элемент навигационная цепочка
 * Пример:
 * <blockquote><pre>
 *     Breadcrumb.click("Название элемента")
 * </blockquote></pre>
 * Ищет навигационную цепочку вида 'Родитель -> Ребенок -> ...',
 * находит в ней элемент соответствующий тексту label и делает по нему клик
 **/
public class Breadcrumb implements TypifiedElement {
    private final ElementsCollection breadcrumbItems = $$x("//*[@role = 'link' or contains(@class, 'BreadcrumbLabel')]");

    public static void click(String label) {
        Button.byElement(new Breadcrumb().getItem(label)).click();
    }

    public static List<String> getItemsText(){
        return new Breadcrumb().breadcrumbItems.texts();
    }

    public static void getItem(int index) {
        Button.byElement(new Breadcrumb().breadcrumbItems.get(index)).click();
    }

    @Step("Получение BreadcrumbItem по label {label}")
    private SelenideElement getItem(String label) {
        return breadcrumbItems.find(Condition.exactText(label));
    }

}
