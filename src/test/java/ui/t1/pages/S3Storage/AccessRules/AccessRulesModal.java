package ui.t1.pages.S3Storage.AccessRules;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal;

public class AccessRulesModal extends AbstractLayerS3<AccessRulesModal> {

    public enum AccessRulesTypes {
        READ("чтение"),
        WRITE("запись"),
        READACL("чтение ACL"),
        WRITEACL("запись ACL");

        private final String rule;

        AccessRulesTypes(String rule) {
            this.rule = rule;
        }

        public String getRule() {
            return rule;
        }
    }

    public AccessRulesModal()
    {

    }

    public AccessRulesModal setUser(String userName){
        SearchSelect.byPlaceholder("Пользователи и сервисные аккаунты").set(userName);
        return this;
    }

    public AccessRulesModal setRules(Boolean isChecked, AccessRulesTypes... rules){
        for (AccessRulesTypes rule : rules) {
            CheckBox.byLabel(rule.getRule()).setChecked(isChecked);
        }
        return this;
    }

    public AccessRulesModal checkRule(Boolean isChecked, AccessRulesTypes rule){
        if (isChecked)
            CheckBox.byLabel(rule.getRule()).getElement().$x("..//input").shouldBe(Condition.checked);
        else
            CheckBox.byLabel(rule.getRule()).getElement().$x("..//input").shouldNotBe(Condition.checked);
        return this;
    }

    public AccessRulesLayer createAccessRule(){
        Button.byText("Создать").click();
        Alert.green("Правило доступа успешно добавлено");
        return new AccessRulesLayer();
    }

    public AccessRulesLayer saveAccessRule(){
        Button.byText("Сохранить").click();
        Alert.green("Правило доступа успешно обновлено");
        return new AccessRulesLayer();
    }

    public AccessRulesLayer closeAccessRule(){
        Button.byText("Закрыть").click();
        return new AccessRulesLayer();
    }

}
