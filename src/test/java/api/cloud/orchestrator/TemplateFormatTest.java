package api.cloud.orchestrator;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.orchestrator.OrchestratorSteps.checkJinja2Template;
import static steps.orchestrator.OrchestratorSteps.checkPythonTemplate;
import static steps.productCatalog.Jinja2Steps.createJinja;
import static steps.productCatalog.PythonTemplateSteps.createPythonTemplateByName;

@Tags({@Tag("product_catalog"), @Tag("regress")})
@Epic("Продуктовый каталог")
@Feature("Оркестратор")
@DisabledIfEnv("prod")
public class TemplateFormatTest extends Tests {

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
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_one_line_python_template_api_test");
        pythonTemplate.setPythonCode("config['value_1'] + config['value_2']");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));

        Integer result = checkPythonTemplate(pythonTemplate).assertStatus(200).jsonPath().getObject("formatted", Integer.class);
        assertEquals(40, result, "Результат вычислений python_code не совпадает с ожидаемым");
    }

    @DisplayName("Проверка шаблона python c незаданными параметрами")
    @TmsLink("SOUL-8894")
    @Test
    public void checkPythonTemplateWithNotDefinedArgsTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_python_template_with_not_defined_api_test");
        pythonTemplate.setPythonCode("config1['value_1'] + config['value_2']");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));

        String errorMessage = checkPythonTemplate(pythonTemplate).assertStatus(200).jsonPath().getString("error");
        assertEquals(errorMessage, "invalid expression vars: name 'config1' is not defined", "Сообщение об ошибке не соответствует ожидаемому");
    }

    @DisplayName("Проверка шаблона python c незаданными полем в теле запроса")
    @TmsLink("SOUL-8895")
    @Test
    public void checkPythonTemplateWithNotDefinedFieldInBodyTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName("check_python_template_with_not_defined_field_in_body_api_test");
        pythonTemplate.setPythonCode("config1['value_1'] + config['value_2']");
        pythonTemplate.setPythonData(new JSONObject("{\"config\":{\"value_1\":10,\"value_2\":30}}"));
        JSONObject template = new JSONObject().put("template", pythonTemplate.getPythonCode());

        String errorMessage = checkPythonTemplate(template).assertStatus(400).jsonPath().getString("details");
        assertEquals(errorMessage, "Exception in python formatting, original: 'data'", "Сообщение об ошибке не соответствует ожидаемому");

        JSONObject data = new JSONObject().put("data", pythonTemplate.getPythonData());

        errorMessage = checkPythonTemplate(data).assertStatus(400).jsonPath().getString("details");
        assertEquals(errorMessage, "Exception in python formatting, original: 'template'", "Сообщение об ошибке не соответствует ожидаемому");
    }

    @DisplayName("Проверка шаблона jinja2 c незаданными полем в теле запроса")
    @TmsLink("SOUL-8932")
    @Test
    public void checkJinja2TemplateWithNotDefinedFieldInBodyTest() {
        Jinja2Template jinja2Template = createJinja("check_jinja2_template_with_not_defined_field_in_body_api_test");
        jinja2Template.setJinja2Template("{%- set acl_fullname = [] -%}\r\n{%- for policy in policies -%}\r\n{%- do acl_fullname.append(project_name +\"-\"+ policy) -%}\r\n{%- endfor -%}\r\n{{ acl_fullname | tojson(indent=2) }}");
        jinja2Template.setJinja2Data("{\n" +
                "        \"name\": \"cloud-crux-polrs4\",\n" +
                "        \"policies\": [\n" +
                "            \"portal-ro\",\n" +
                "            \"user-ro\"\n" +
                "        ],\n" +
                "        \"project_name\": \"kv-proj-k0x1pshcki\"\n" +
                "    }");

        JSONObject template = new JSONObject().put("template", jinja2Template.getJinja2Template());

        String errorMessage = checkJinja2Template(template).assertStatus(400).jsonPath().getString("details");
        assertEquals("Exception in jinja2 formatting, original: 'data'", errorMessage, "Сообщение об ошибке не соответствует ожидаемому");

        JSONObject data = new JSONObject().put("data", jinja2Template.getJinja2Data());

        errorMessage = checkJinja2Template(data).assertStatus(400).jsonPath().getString("details");
        assertEquals("Exception in jinja2 formatting, original: 'template'", errorMessage, "Сообщение об ошибке не соответствует ожидаемому");
    }
}
