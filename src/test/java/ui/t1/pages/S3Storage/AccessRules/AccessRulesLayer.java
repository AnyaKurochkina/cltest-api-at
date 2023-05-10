package ui.t1.pages.S3Storage.AccessRules;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.S3Storage.CORS.CORSLayer;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessRulesLayer extends AbstractLayerS3<AccessRulesLayer> {

    private DataTable accessRulesList;

    private final Integer menuIdx = 2;
    private final String fRuleName = "Пользователи";

    public AccessRulesLayer(String name)
    {
        super(name);
    }

    public AccessRulesLayer()
    {

    }

    @Step("Открытие модального окна правила доступа")
    public AccessRulesModal addAccessRule()
    {
        Button.byText("Добавить").click();
        return new AccessRulesModal();
    }

    @Step("Удаление правила доступа")
    public AccessRulesLayer deleteAccessRule(String ruleName)
    {
        accessRulesList = new DataTable(fRuleName);

        Menu.byElement(accessRulesList.getRowByColumnValue(fRuleName, ruleName)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Удалить");

        Dialog.byTitle("Удалить правило доступа").clickButton("Удалить");
        Alert.green("Правило доступа удалено");
        return new AccessRulesLayer("Правила доступа");
    }

    @Step("Удаление правила доступа")
    public AccessRulesModal editAccessRule(String ruleName)
    {
        accessRulesList = new DataTable(fRuleName);

        Menu.byElement(accessRulesList.getRowByColumnValue(fRuleName, ruleName)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Редактировать");

        return new AccessRulesModal();
    }

    @Step("Проверка наличия правила '{ruleName}' в списке - '{isExists}'")
    public AccessRulesLayer checkRule(String ruleName, Boolean isExists){
        DataTable bucketList = new DataTable(fRuleName);
        if (isExists)
            assertTrue(bucketList.isColumnValueEquals(fRuleName,ruleName));
        else
            assertFalse(bucketList.isColumnValueEquals(fRuleName,ruleName));
        return this;
    }
}
