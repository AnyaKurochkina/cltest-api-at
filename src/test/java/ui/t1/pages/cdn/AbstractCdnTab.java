package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.AbstractEntity;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.Menu;

public abstract class AbstractCdnTab<T extends AbstractCdnTab<?, ?>, C extends AbstractEntity> {

    protected final Button addButton = Button.byLabel("Добавить");
    private final String counterXpath = "//*[text()='%s']//following-sibling::span[contains(@class, 'styles__ItemCountStyled')]";

    abstract public void create(C abstractCdnEntity);

    abstract public T delete(String entityName);

    abstract public String getMainTableName();

    protected void chooseActionFromMenu(String name, String actionName) {
        DataTable table = new DataTable("Источники");
        Menu.byElement(table.searchAllPages(t -> table.isColumnValueContains("Название", name))
                        .getRowByColumnValueContains("Название", name)
                        .get()
                        .$x(".//button[@id = 'actions-menu-button']"))
                .select(actionName);
    }

    @SuppressWarnings("unchecked")
    @Step("[Проверка] существования CDN сущности с именем: {0}")
    public T checkCdnEntityExistByName(String entityName) {
        new DataTable("Название").asserts().checkColumnContainsValue("Название", entityName);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Step("[Проверка] отсутствия CDN сущности")
    public T checkThatCdnEntityDoesNotExist(String entityName) {
        new DataTable("Название").asserts().checkColumnNotContainsValue("Название", entityName);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Step("[Проверка] Количество сущностей в ui счетчике совпадает с ожидаемым количеством сущностей")
    public T checkCounter(String expectedCount) {
        SelenideElement uiCounter = Selenide.$x(String.format(counterXpath, getMainTableName()));
        uiCounter.shouldHave(Condition.text(expectedCount)
                .because(String.format("В таблице: %s, счетчик должен отображать ожидаемое количество сущностей - %s, а отображает - %s",
                        getMainTableName(), expectedCount, uiCounter.getText())));
        return (T) this;
    }
}
