package ui.t1.pages.S3Storage.LifeCycle;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

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
        DAYSAMMOUNT("Количество дней"),
        EXACTDATE("Точная дата"),
        EXPIREDJBJECTDELETEMARKER("Expired object delete marker");

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

    @Step("Проверка префикса правила {prefix}")
    public LifeCycleModal checkPrefix(String prefix)
    {
        Assertions.assertEquals(prefix, Input.byName("prefix").getValue());
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

    @Step("Проверка дней правила {days}")
    public LifeCycleModal checkDays(String days)
    {
        Assertions.assertEquals(days, Input.byName("days").getValue());
        return this;
    }

    @Step("Закрытие модального окна ЖЦ")
    public LifeCycleLayer closeLifeCycleModal()
    {
        Button.byText("Закрыть").click();
        return new LifeCycleLayer();
    }

    @Step("Установка триггера ЖЦ")
    public LifeCycleModal setConditionalTrigger(LifeCycleConditionTriggers trigger)
    {
        Radio.byValue(trigger.getCondition()).checked();
        return this;
    }

    @Step("Создать ЖЦ")
    public LifeCycleLayer createLifeCycle()
    {
        Button.byText("Создать").click();
        Alert.green("Правило жизненного цикла успешно создано");
        return new LifeCycleLayer("Жизненный цикл");
    }

    @Step("Создать ЖЦ")
    public LifeCycleLayer updateLifeCycle()
    {
        Button.byText("Обновить").click();
        Alert.green("Правило жизненного цикла успешно обновлено");
        return new LifeCycleLayer("Жизненный цикл");
    }

}
