package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import core.helper.http.AssertResponse;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.visualTeamplate.*;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import steps.productCatalog.VisualTemplateSteps;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.VisualTemplateSteps.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json");
    CompactTemplate compactTemplate = CompactTemplate.builder()
            .name(new Name("name"))
            .type(new Type("type", "label"))
            .status(new Status("status"))
            .build();
    FullTemplate fullTemplate = FullTemplate.builder()
            .type("type")
            .value(Arrays.asList("value", "value2"))
            .build();

    @DisplayName("Создание шаблона визуализации в продуктовом каталоге")
    @TmsLink("643631")
    @Test
    public void createVisualTemplateTest() {
        String name = "item_visual_template_test_api-:2022.";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        ItemVisualTemplate getVisualTemplate = getVisualTemplateById(visualTemplates.getId());
        assertEquals(visualTemplates, getVisualTemplate);
    }

    @DisplayName("Удаление шаблона визуализации со статусом is_active=true")
    @TmsLink("742485")
    @Test
    public void deleteIsActiveTemplate() {
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name("delete_with_active_true_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("gitlab"))
                .eventType(Collections.singletonList("vm"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        AssertResponse.run(() -> deleteVisualTemplateById(visualTemplates.getId())).status(403)
                .responseContains("Deletion not allowed (is_active=True)");
        partialUpdateVisualTemplate(visualTemplates.getId(), new JSONObject().put("is_active", false));
    }

    @DisplayName("Проверка существования шаблона визуализации по имени")
    @TmsLink("682865")
    @Test
    public void checkVisualTemplateExists() {
        String name = "exist_item_visual_template_test_api";
        ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        Assertions.assertTrue(isVisualTemplateExists(name));
        Assertions.assertFalse(isVisualTemplateExists("no_exist_template_test"));
    }

    @DisplayName("Проверка сортировки по дате создания в шаблонах визуализации")
    @TmsLink("742486")
    @Test
    public void orderingByCreateData() {
        List<ItemVisualTemplate> list = getItemVisualTemplateListOrdering("create_dt");
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в шаблонах визуализации")
    @TmsLink("742490")
    @Test
    public void orderingByUpDateData() {
        List<ItemVisualTemplate> list = getItemVisualTemplateListOrdering("update_dt");
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpdateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpdateDt());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в шаблонах отображения")
    @TmsLink("742492")
    @Test
    public void checkAccessWithPublicToken() {
        String name = "check_access_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
//        steps.getObjectByNameWithPublicToken(name).assertStatus(200);
//        steps.createProductObjectWithPublicToken(steps
//                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
//        steps.partialUpdateObjectWithPublicToken(visualTemplates.getId(),
//                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
//        steps.putObjectByIdWithPublicToken(visualTemplates.getId(), steps
//                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
//        steps.deleteObjectWithPublicToken(visualTemplates.getId()).assertStatus(403);
    }

    @DisplayName("Получение шаблона визуализации по Id")
    @TmsLink("643644")
    @Test
    public void getVisualTemplateByIdTest() {
        String name = "get_by_id_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        ItemVisualTemplate productCatalogGet = getVisualTemplateById(visualTemplates.getId());
        assertEquals(name, productCatalogGet.getName());
    }

    @DisplayName("Копирование шаблона визуализации по Id")
    @TmsLink("682886")
    @Test
    public void copyVisualTemplateByIdTest() {
        String name = "copy_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("gitlab"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        String cloneName = visualTemplates.getName() + "-clone";
        copyVisualTemplateById(visualTemplates.getId());
        String cloneId = getVisualTemplateByName(cloneName).getId();
        boolean isActive = steps.getJsonPath(cloneId).get("is_active");
        assertFalse(isActive);
        deleteVisualTemplateByName(cloneName);
        Assertions.assertFalse(isVisualTemplateExists(cloneName));
        steps.partialUpdateObject(visualTemplates.getId(), new JSONObject().put("is_active", false));
    }

    @DisplayName("Проверка tag_list при копировании шаблона визуализации")
    @TmsLink("SOUL-7005")
    @Test
    public void copyVisualTemplateAndCheckTagListTest() {
        String visualTemplateName = "clone_visual_template_and_check_tag_list_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(visualTemplateName)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .tagList(Arrays.asList("test", "api_test"))
                .build()
                .createObject();
        ItemVisualTemplate cloneTemplate = copyVisualTemplateById(visualTemplates.getId());
        deleteVisualTemplateById(cloneTemplate.getId());
        assertEquals(visualTemplates.getTagList(), cloneTemplate.getTagList());
    }

    @DisplayName("Экспорт шаблона визуализации по Id")
    @TmsLink("643667")
    @Test
    public void exportVisualTemplateByIdTest() {
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name("export_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        exportVisualTemplateById(visualTemplates.getId());
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте шаблона визуализации")
    @TmsLink("SOUL-7086")
    @Test
    public void checkExportedObjectsFieldVisualTemplateTest() {
        String visualTemplateName = "visual_template_exported_objects_test_api";
        ItemVisualTemplate visualTemplate = createVisualTemplate(visualTemplateName);
        Response response = exportVisualTemplateById(visualTemplate.getId());
        LinkedHashMap r = response.jsonPath().get("exported_objects.ItemVisualisationTemplate.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(visualTemplate.getName(), jsonObject.get("name").toString());
    }

    @DisplayName("Частичное обновление шаблона визуализации по Id")
    @TmsLink("643668")
    @Test
    public void partialUpdateVisualTemplateTest() {
        String name = "partial_update_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        String expectedDescription = "UpdateDescription";
        partialUpdateVisualTemplate(visualTemplates.getId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        ItemVisualTemplate getResponse = getVisualTemplateById(visualTemplates.getId());
        String actualDescription = getResponse.getDescription();
        assertEquals(expectedDescription, actualDescription);
    }

    @DisplayName("Обновление default_item шаблона визуализации")
    @TmsLink("1127554")
    @Test
    public void updateDefaultItemVisualTemplate() {
        String name = "update_default_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .defaultItem(new LinkedHashMap<String, Object>() {{
                    put("default_key", "value_key");
                }})
                .isActive(false)
                .build()
                .createObject();
        LinkedHashMap<String, Object> defaultItem = new LinkedHashMap<String, Object>() {{
            put("default_key", "value_key");
            put("default_key2", "value_key2");
        }};

        JSONObject json = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .defaultItem(defaultItem)
                .isActive(false)
                .build()
                .toJson();
        partialUpdateVisualTemplate(visualTemplates.getId(), json);
        ItemVisualTemplate getResponse = getVisualTemplateById(visualTemplates.getId());
        assertEquals(defaultItem, getResponse.getDefaultItem());
    }

    @DisplayName("Получение шаблона визуализации по event_provider, event_type")
    @TmsLink("643671")
    @Test
    public void getVisualTemplateByProviderAndType() {
        String name = "get_by_provider_type_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("hcp"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        ItemVisualTemplate visualTemplate = getItemVisualTemplateByTypeProvider(visualTemplates.getEventType().get(0), visualTemplates.getEventProvider().get(0));
        VisualTemplateSteps.partialUpdateVisualTemplate(visualTemplates.getId(), new JSONObject().put("is_active", false));
        assertEquals(visualTemplate.getEventProvider(), visualTemplate.getEventProvider());
        assertEquals(visualTemplate.getEventType(), visualTemplate.getEventType());
    }

    @DisplayName("Проверка отсутствия поля default_order в ответе на запрос get item_visual_template/eventType/eventProvider/")
    @TmsLink("1467526")
    @Test
    public void checkIsDefaultOrderIsNotExistsTest() {
        String name = "check_is_default_order_is_not_exist_item_visual_template_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("hcp"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        ItemVisualTemplate visualTemplate = getItemVisualTemplateByTypeProvider(visualTemplates.getEventType().get(0), visualTemplates.getEventProvider().get(0));
        VisualTemplateSteps.partialUpdateVisualTemplate(visualTemplates.getId(), new JSONObject().put("is_active", false));
        assertNull(visualTemplate.getDefaultOrder());
    }

    @DisplayName("Сортировка шаблонов визуализации по статусу")
    //todo убрать логику
    @TmsLink("1086581")
    @Test
    public void orderingByStatus() {
        List<ItemVisualTemplate> list = getItemVisualTemplateListOrdering("status");
        boolean result = false;
        int count = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            ItemVisualTemplate item = list.get(i);
            ItemVisualTemplate nextItem = list.get(i + 1);
            if (item.getIsActive().equals(nextItem.getIsActive())) {
                result = true;
            } else {
                count++;
            }
            if (count > 1) {
                result = false;
                break;
            }
        }
        assertTrue(result, "Список не отсортирован.");
    }

    @DisplayName("Проверка на наличие ключей в FullTemplate")
    @TmsLink("742494")
    @Test
    public void fullTemplateFields() {
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name("check_full_template_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        assertEquals("type", visualTemplates.getFullTemplate().getType());
        assertEquals(Arrays.asList("value", "value2"), visualTemplates.getFullTemplate().getValue());
    }

    @DisplayName("Проверка на наличие ключей в CompactTemplate")
    @TmsLink("742495")
    @Test
    public void compactTemplateFields() {
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name("check_compact_template_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        assertEquals("name", visualTemplates.getCompactTemplate().getName().getValue());
        assertEquals("status", visualTemplates.getCompactTemplate().getStatus().getValue());
        assertEquals("type", visualTemplates.getCompactTemplate().getType().getValue());
    }

    @DisplayName("Удаление шаблона визуализации")
    @TmsLink("643674")
    @Test
    public void deleteTemplate() {
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name("delete_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        deleteVisualTemplateById(visualTemplates.getId());
    }

    @Test
    @DisplayName("Загрузка VisualTemplate в GitLab")
    @Disabled
    @TmsLink("975416")
    public void dumpToGitlabVisualTemplate() {
        String visualTemplateName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        ItemVisualTemplate visualTemplate = ItemVisualTemplate.builder()
                .name(visualTemplateName)
                .title(visualTemplateName)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        String tag = "itemvisualisationtemplate_" + visualTemplateName;
        Response response = steps.dumpToBitbucket(visualTemplate.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @Disabled
    @DisplayName("Выгрузка VisualTemplate из GitLab")
    @TmsLink("1029469")
    public void loadFromGitlabVisualTemplate() {
        String visualTemplateName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        ItemVisualTemplate visualTemplate = ItemVisualTemplate.builder()
                .name(visualTemplateName)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        Response response = steps.dumpToBitbucket(visualTemplate.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteVisualTemplateByName(visualTemplateName);
        String path = "itemvisualisationtemplate_" + visualTemplateName;
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(isVisualTemplateExists(visualTemplateName));
        deleteVisualTemplateByName(visualTemplateName);
        assertFalse(isVisualTemplateExists(visualTemplateName));
    }

    @DisplayName("Проверка отсутствия поля default_item в шаблона визуализации по event_provider, event_type")
    @TmsLink("1078708")
    @Test
    public void getVisualTemplateByProviderAndTypeAndCheckDefaultItemIsNull() {
        String name = "get_by_provider_type_item_visual_template_and_check_default_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        ItemVisualTemplate visualTemplate = getItemVisualTemplateByTypeProvider(visualTemplates.getEventType().get(0), visualTemplates.getEventProvider().get(0));
        partialUpdateVisualTemplate(visualTemplates.getId(), new JSONObject().put("is_active", false));
        assertNull(visualTemplate.getDefaultItem());
    }
}
