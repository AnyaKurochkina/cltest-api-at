package ui.t1.pages.S3Storage.AccessRules;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.S3Storage.CORS.CORSLayer;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal;

public class AccessRulesLayer extends AbstractLayerS3<AccessRulesLayer> {

    private DataTable accessRulesList;

    private final Integer menuIdx = 2;

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
        accessRulesList = new DataTable("Пользователи");

        Menu.byElement(accessRulesList.getRowByColumnValue("Пользователи", ruleName)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Удалить");

        Dialog.byTitle("Удалить правило доступа").clickButton("Удалить");
        Alert.green("Правило доступа удалено");
        return new AccessRulesLayer("Правила доступа");
    }

    @Step("Удаление правила доступа")
    public AccessRulesModal editAccessRule(String ruleName)
    {
        accessRulesList = new DataTable("Пользователи");

        Menu.byElement(accessRulesList.getRowByColumnValue("Пользователи", ruleName)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Редактировать");

        return new AccessRulesModal();
    }
}
