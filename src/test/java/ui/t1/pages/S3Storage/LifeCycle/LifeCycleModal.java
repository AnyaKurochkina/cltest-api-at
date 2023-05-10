package ui.t1.pages.S3Storage.LifeCycle;

import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.CheckBox;
import ui.elements.Input;
import ui.elements.Select;

public class LifeCycleModal {

    public LifeCycleModal()
    {

    }

    public enum LifeCycleRuleTypes {
        EXPIRATION("Expiration"),
        NONCURRENTVERSIONEXPIRATION("NoncurrentVersionExpiration"),
        ABORTCOMPLETEMULTIPARTUPLOAD("AbortCompleteMultipartUpload");

        private final String rule;

        LifeCycleRuleTypes(String rule) {
            this.rule = rule;
        }

        public String getRule() {
            return rule;
        }
    }

    public enum LifeCycleConditionTriggers {
        DAYSAMMOUNT("Expiration"),
        EXACTDATE("NoncurrentVersionExpiration"),
        EXPIREDJBJECTDELETEMARKER("AbortCompleteMultipartUpload");

        private final String condition;

        LifeCycleConditionTriggers(String condition) {
            this.condition = condition;
        }

        public String getCondition() {
            return condition;
        }
    }

    @Step("Добавление названия правила {name}")
    public LifeCycleModal setName(String name)
    {
        Input.byName("name").setValue(name);
        return this;
    }

    @Step("Добавление префикса правила {prefix}")
    public LifeCycleModal setPrefix(String prefix)
    {
        Input.byName("prefix").setValue(prefix);
        return this;
    }

    @Step("Добавление правила жизненного цикла {ruleType}")
    public LifeCycleModal setRuleType(LifeCycleRuleTypes ruleType)
    {
        Select.byLabel("Тип правила").set(ruleType.getRule());
        return this;
    }

    @Step("Добавление дней {days}")
    public LifeCycleModal setDays(String days)
    {
        Input.byName("days").setValue(days);
        return this;
    }

    @Step("Установка триггера ЖЦ")
    public LifeCycleModal setConditionalTrigger(LifeCycleConditionTriggers trigger)
    {
        CheckBox.byLabel(trigger.getCondition()).setChecked(true);
        return this;
    }

    @Step("Создать ЖЦ")
    public LifeCycleLayer createLifeCycle()
    {
        Button.byText("Создать").click();
        return new LifeCycleLayer("Жизненный цикл");
    }

}
