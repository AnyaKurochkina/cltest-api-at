package ui.t1.pages.S3Storage.CORS;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.MultiSelect;
import ui.elements.Select;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class CORSModal  {

    public enum Method {
        GET("GET"),
        DELETE("DELETE"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD");

        private final String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }

    public enum AccessControls {
        FIVESECS("5 сек"),
        TENSECS("10 сек"),
        FIFTEENSECS("15 сек"),
        ONEMIN("1 мин"),
        FIVEMINS("5 мин");

        private final String time;

        AccessControls(String time) {
            this.time = time;
        }

        public String getTime() {
            return time;
        }
    }

    @Step("Добавление нового значения Origins '{CORSname}' с признаком очистки поля '{clear}'")
    public CORSModal setCORSOrigins(String CORSname, Boolean clear)
    {
        MultiSelect.byLabel("Origins").add(CORSname, clear);
        return new CORSModal();
    }

    @Step("Проверка значения Origins '{Origins}'")
    public CORSModal checkCORSOrigins(String Origins)
    {
        String value = MultiSelect.byLabel("Origins").getValue();
        Assertions.assertEquals(Origins, value);
        return this;
    }

    @Step("Добавление нового значения Allowed Method '{allowedMethod}'")
    public CORSModal setAllowedMethods(String... allowedMethods)
    {
        MultiSelect.byLabel("Allowed Methods").set(allowedMethods);
        return new CORSModal();
    }

    @Step("Проверка значения Allowed Methods '{Origins}'")
    public CORSModal checkAllowedMethods(String Origins)
    {
        String value = MultiSelect.byLabel("Allowed Methods").getValues(4);
        Assertions.assertEquals(Origins, value);
        return this;
    }

    @Step("Добавление нового значения Allowed Headers '{allowedHeaders}' с признаком очистки поля '{clear}'")
    public CORSModal setAllowedHeaders(String allowedHeaders, Boolean clear)
    {
        MultiSelect.byLabel("Allowed Headers (не обязательно)").add(allowedHeaders, clear);
        return new CORSModal();
    }

    @Step("Добавление нового значения ExposeHeaders '{exposeHeaders}' с признаком очистки поля '{clear}'")
    public CORSModal setExposeHeaders(String exposeHeaders, Boolean clear)
    {
        MultiSelect.byLabel("Expose Headers (не обязательно)").add(exposeHeaders, clear);
        return new CORSModal();
    }

    @Step("Добавление нового значения Access Control '{accessControl}'")
    public CORSModal setAccessControl(AccessControls accessControl)
    {
        Select.byLabel("Access Control Max Age (не обязательно)").set(accessControl.getTime());
        return new CORSModal();
    }

    @Step("Закрытие модального окна CORS")
    public CORSLayer closeCORSModal()
    {
        Button.byText("Закрыть").click();
        return new CORSLayer("CORS");
    }

    @Step("Создание CORS")
    public CORSLayer createCORS()
    {
        Button.byText("Создать").click();
        return new CORSLayer("CORS");
    }

    @Step("Обновление CORS")
    public CORSLayer renewCORS()
    {
        Button.byText("Обновить").click();
        return new CORSLayer("CORS");
    }
}
