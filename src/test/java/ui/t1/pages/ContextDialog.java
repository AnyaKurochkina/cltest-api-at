package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.IAM.OrgStructurePage;

import java.util.Arrays;
import java.util.List;

import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.*;


public class ContextDialog extends Dialog {
    String favoriteIconXpath = ".//*[name() = 'svg' and @cursor ='pointer']/parent::div";
    Button all = Button.byId("All");
    Button favorite = Button.byId("Favorite");
    Button recent = Button.byId("Recent");
    Button managingOrgStructure = Button.byText("Управление орг. структурой");
    SelenideElement organization = $x("//*[name()='path' and @d = 'M12.714 3.23a1.35 1.35 0 00-1.448.001L4.563 7.495c-1.14.725-.626 2.489.725 2.489h13.42c1.351 0 1.864-1.765.723-2.49L12.714 3.23zm-.75 1.098a.05.05 0 01.054 0l6.716 4.264a.077.077 0 01.02.016l.002.003.001.002a.061.061 0 01-.001.035.062.062 0 01-.018.03l-.003.002h-.002a.078.078 0 01-.025.004H5.288a.078.078 0 01-.026-.003l-.005-.003a.062.062 0 01-.017-.03.062.062 0 01-.002-.035l.003-.005a.078.078 0 01.02-.016l6.703-4.264z']/ancestor::div[@class='title-wrapper']");

    public ContextDialog() {
        super("Выберите контекст");
        assertTrue(favorite.isVisible());
        assertTrue(all.isVisible());
        assertTrue(recent.isVisible());
    }

    @Step("Переход во вкладку \"Все\"")
    public ContextDialog goToAllTab() {
        all.click();
        assertEquals("true", all.getButton().getAttribute("aria-selected"));
        checkHeaders(Arrays.asList("Название", "Тип", "Идентификатор"));
        return this;
    }

    @Step("Переход во вкладку \"Избранное\"")
    public ContextDialog goToFavoriteTab() {
        favorite.click();
        assertEquals("true", favorite.getButton().getAttribute("aria-selected"));
        checkHeaders(Arrays.asList("Название", "Идентификатор"));
        return this;
    }

    @Step("Переход во вкладку \"Недавнее\"")
    public ContextDialog goToRecentTab() {
        recent.click();
        assertEquals("true", recent.getButton().getAttribute("aria-selected"));
        checkHeaders(Arrays.asList("Название", "Идентификатор"));
        return this;
    }

    @Step("Добавить ресуср {name} в избранное")
    public ContextDialog addToFavorite(String name) {
        Waiting.sleep(2000);
        SelenideElement favoriteIcon = new OrgTable().getRowByColumnValue("Идентификатор", name).get().$x(favoriteIconXpath);
        favoriteIcon.hover();
        Waiting.sleep(1000);
        assertEquals("В избранное", new Tooltip().toString());
        favoriteIcon.click();
        Alert.green(StringUtils.format("Ресурс {} успешно добавлен в избранное", name));
        return this;
    }

    @Step("Удалить ресурс {name} из избранного")
    public ContextDialog removeFromFavorite(String name) {
        Waiting.sleep(2000);
        SelenideElement favoriteIcon = new OrgTable().getRowByColumnValue("Идентификатор", name).get().$x(favoriteIconXpath);
        favoriteIcon.hover();
        Waiting.sleep(1000);
        assertEquals("Удалить из избранного", new Tooltip().toString());
        favoriteIcon.click();
        Alert.green(StringUtils.format("Ресурс {} успешно удален из избранного", name));
        assertFalse(new OrgTable().isColumnValueEquals("Идентификатор", name));
        return this;
    }

    @Step("Сменить контекст на {orgName}")
    public IndexPage changeOrganization(String orgName) {
        $x("//*[@id='selectValueWrapper']").click();
        $x("//div[contains(text(), '{}')]", orgName).shouldBe(Condition.visible).click();
        all.click();
        organization.shouldBe(Condition.visible).click();
        assertTrue($x("//div[contains(text(), '{}')]", orgName).isDisplayed());
        return new IndexPage();
    }

    @Step("Выбрать огранизацию с именем {orgName}")
    public ContextDialog selectOrganization(String orgName) {
        $x("//*[@id='selectValueWrapper']").click();
        $x("//div[contains(text(), '{}')]", orgName).shouldBe(Condition.visible).click();
        Waiting.sleep(1000);
        return this;
    }

    public IndexPage changeContext(String name) {
        Waiting.sleep(1000);
        Table.Row r = new OrgTable().getRowByColumnValue("Название", name);
        String title = r.getValueByColumn("Идентификатор");
        String type = getContextType(title);
        r.get().$x(".//p[@color = 'Primary/Primary 60 Main']").parent().click();
        Alert.green(StringUtils.format("Выбран контекст: {} \"{}\"", type, name));
        return new IndexPage();
    }

    public OrgStructurePage goToOrgStructure() {
        managingOrgStructure.click();
        return new OrgStructurePage();
    }

    private void checkHeaders(List<String> headers) {
        assertEquals(headers, new OrgTable().getNotEmptyHeaders());
    }

    private String getContextType(String s) {
        if (s.startsWith("fold")) {
            return "Папка";
        }
        if (s.startsWith("proj")) {
            return "Проект";
        }
        return "Организация";
    }

    private static class OrgTable extends Table {
        public OrgTable() {
            super("Название", -1);
        }
    }
}
