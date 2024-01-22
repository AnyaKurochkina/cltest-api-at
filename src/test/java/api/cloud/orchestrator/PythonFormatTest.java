package api.cloud.orchestrator;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.orchestrator.OrchestratorSteps.checkPythonTemplate;
import static steps.productCatalog.PythonTemplateSteps.createPythonTemplateByName;

@Tags({@Tag("product_catalog"), @Tag("regress")})
@Epic("Продуктовый каталог")
@Feature("Оркестратор")
@DisabledIfEnv("prod")
public class PythonFormatTest extends Tests {

    @DisplayName("Проверка многострочного шаблона python")
    @TmsLink("SOUL-8892")
    @Test
    public void checkPythonTemplateTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_python_template_api_test");
        pythonTemplate.setPythonCode("sum = config['value_1'] + config['value_2']\n" +
                "sum += 100\n" +
                "return sum");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));

        Integer result = checkPythonTemplate(pythonTemplate).assertStatus(200).jsonPath().getObject("formatted", Integer.class);
        assertEquals(140, result, "Результат вычислений python_code не совпадает с ожидаемым");
    }

    @DisplayName("Проверка однострочного шаблона python")
    @TmsLink("SOUL-8893")
    @Test
    public void checkOneLinePythonTemplateTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_python_template_api_test");
        pythonTemplate.setPythonCode("config['value_1'] + config['value_2']");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));

        Integer result = checkPythonTemplate(pythonTemplate).assertStatus(200).jsonPath().getObject("formatted", Integer.class);
        assertEquals(40, result, "Результат вычислений python_code не совпадает с ожидаемым");
    }

    @DisplayName("Проверка шаблона python c незаданными параметрами")
    @TmsLink("SOUL-8894")
    @Test
    public void checkPythonTemplateWithNotDefinedArgsTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_python_template_api_test");
        pythonTemplate.setPythonCode("config1['value_1'] + config['value_2']");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));

        String errorMessage = checkPythonTemplate(pythonTemplate).assertStatus(200).jsonPath().getString("error");
        assertEquals(errorMessage, "invalid expression vars: name 'config1' is not defined", "Сообщение об ошибке не соответствует ожидаемому");
    }

    @DisplayName("Проверка шаблона python c незаданными полем в теле запроса")
    @TmsLink("SOUL-8895")
    @Test
    public void checkPythonTemplateWithNotDefinedFieldInBodyTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_python_template_api_test");
        pythonTemplate.setPythonCode("config1['value_1'] + config['value_2']");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));
        JSONObject template = new JSONObject().put("template", pythonTemplate.getPythonCode());

        String errorMessage = checkPythonTemplate(template).assertStatus(400).jsonPath().getString("details");
        assertEquals(errorMessage, "Exception in python formatting, original: 'data'", "Сообщение об ошибке не соответствует ожидаемому");

        JSONObject data = new JSONObject().put("data", pythonTemplate.getPythonData());

        errorMessage = checkPythonTemplate(data).assertStatus(400).jsonPath().getString("details");
        assertEquals(errorMessage, "Exception in python formatting, original: 'template'", "Сообщение об ошибке не соответствует ожидаемому");
    }
}
