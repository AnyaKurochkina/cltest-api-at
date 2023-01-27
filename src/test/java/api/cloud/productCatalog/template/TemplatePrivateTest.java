package api.cloud.productCatalog.template;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.TemplatePrivateSteps.*;
import static steps.productCatalog.TemplateSteps.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplatePrivateTest extends Tests {

    @DisplayName("Создание/Получение/Удаление шаблона в продуктовом каталоге c сервисным токеном")
    @TmsLinks({@TmsLink("1420385"), @TmsLink("1420386"), @TmsLink("1420388")})
    @Test
    public void templatePrivateByIdTest() {
        String templateName = "template_private_test_api";
        if (isTemplateExists(templateName)) {
            deleteTemplateByName(templateName);
        }
        JSONObject jsonObject = Template.builder()
                .name(templateName)
                .build()
                .toJson();
        Template template = createTemplatePrivate(jsonObject);
        Integer templateId = template.getId();
        Template actualTemplate = getTemplatePrivateById(templateId);
        assertEquals(template, actualTemplate);
        deleteTemplatePrivateById(templateId);
    }

    @DisplayName("Обновление шаблона c сервисным токеном")
    @TmsLink("1420389")
    @Test
    public void updateTemplatePrivateTest() {
        String description = "test";
        Template template = createTemplateByName("template_update_private_test_api");
        partialUpdatePrivateTemplate(template.getId(), new JSONObject().put("description", "test"));
        Template updatedService = getTemplateById(template.getId());
        assertEquals(description, updatedService.getDescription());
    }

    @DisplayName("Создание/Получение/Удаление шаблона в продуктовом каталоге c сервисным токеном api/v2")
    @TmsLinks({@TmsLink("1420395"), @TmsLink("1420396"), @TmsLink("1420400")})
    @Test
    public void templatePrivateByNameTest() {
        String templateName = "template_private_v2_test_api";
        if (isTemplateExists(templateName)) {
            deleteTemplateByName(templateName);
        }
        JSONObject jsonObject = Template.builder()
                .name(templateName)
                .build()
                .toJson();
        Template template = createTemplatePrivateV2(jsonObject);
        Template actualTemplate = getTemplatePrivateByName(templateName);
        assertEquals(template, actualTemplate);
        deleteTemplatePrivateByName(templateName);
    }

    @DisplayName("Обновление шаблона c сервисным токеном api/v2")
    @TmsLink("1420401")
    @Test
    public void updateTemplatePrivateByNameTest() {
        String templateName = "template_update_private_by_name_test_api";
        String description = "test";
        createTemplateByName(templateName);
        partialUpdateTemplatePrivateByName(templateName, new JSONObject().put("description", "test"));
        Template updatedTemplate = getTemplatePrivateByName(templateName);
        assertEquals(description, updatedTemplate.getDescription());
    }
}
