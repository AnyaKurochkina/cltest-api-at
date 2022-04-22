package tests.productCatalog.visualTemplate;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.*;
import httpModels.productCatalog.itemVisualItem.getVisualTemplate.GetVisualTemplateResponse;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.GetVisualTemplateListResponse;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.ItemVisualTemplates;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json", Configure.ProductCatalogURL);
    CompactTemplate compactTemplate = CompactTemplate.builder().name(new Name("name"))
            .type(new Type("type")).status(new Status("status")).build();
    FullTemplate fullTemplate = FullTemplate.builder().type("type").value(Arrays.asList("value", "value2")).build();

    @DisplayName("Создание шаблона визуализации в продуктовом каталоге")
    @TmsLink("643631")
    @Test
    public void createVisualTemplate() {
        String name = "item_visual_template_test_api-:2022.";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        GetImpl getVisualTemplate = steps.getById(visualTemplates.getItemId(), CreateItemVisualResponse.class);
        assertEquals(name, getVisualTemplate.getName());
    }

    @DisplayName("Удаление шаблона визуализации со статусом is_active=true")
    @TmsLink("742485")
    @Test
    public void deleteIsActiveTemplate() {
        String name = "delete_with_active_true_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        Response deleteResponse = steps.getDeleteObjectResponse(visualTemplates.getItemId())
                .assertStatus(200);
        steps.partialUpdateObject(visualTemplates.getItemId(), new JSONObject().put("is_active", false));
        assertEquals(deleteResponse.jsonPath().get("error"), "Deletion not allowed (is_active=True)");
    }

    @DisplayName("Проверка существования шаблона визуализации по имени")
    @TmsLink("682865")
    @Test
    public void checkVisualTemplateExists() {
        String name = "exist_item_visual_template_test_api";
        ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        Assertions.assertTrue(steps.isExists(name));
        Assertions.assertFalse(steps.isExists("no_exist_template_test"));
    }

    @DisplayName("Проверка сортировки по дате создания в шаблонах визуализации")
    @TmsLink("742486")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps.orderingByCreateData(GetVisualTemplateListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в шаблонах визуализации")
    @TmsLink("742490")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps.orderingByUpDateData(GetVisualTemplateListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в шаблонах отображения")
    @TmsLink("742492")
    @Test
    public void checkAccessWithPublicToken() {
        String name = "check_access_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(name).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(visualTemplates.getItemId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(visualTemplates.getItemId(), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(visualTemplates.getItemId()).assertStatus(403);
    }

    @DisplayName("Получение списка шаблонов визуализаций по фильтрам event_provider и event_type")
    @TmsLink("643638")
    @Test
    public void getVisualTemplateListByProviderAndType() {
        String name = "get_list_by_type_and_provider_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        String providerFilter = visualTemplates.getEventProvider().get(0);
        String typeFilter = visualTemplates.getEventType().get(0);
        List<ItemImpl> list = steps.getProductObjectList(GetVisualTemplateListResponse.class,
                "?event_type=" + typeFilter + "&event_provider=" + providerFilter);
        assertTrue(list.size() > 0);
        for (ItemImpl impl : list) {
            assertTrue(steps.getJsonPath(impl.getId()).getString("event_provider").contains(providerFilter));
            assertTrue(steps.getJsonPath(impl.getId()).getString("event_type").contains(typeFilter));
        }
    }

    @DisplayName("Импорт шаблона визуализации")
    @TmsLink("643640")
    @Test
    public void importVisualTemplate() {
        String data = JsonHelper.getStringFromFile("productCatalog/itemVisualTemplate/visualTemplateImport.json");
        String importName = new JsonPath(data).get("ItemVisualisationTemplate.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/itemVisualTemplate/visualTemplateImport.json");
        assertTrue(steps.isExists(importName));
        steps.deleteByName(importName, GetVisualTemplateListResponse.class);
        assertFalse(steps.isExists(importName));
    }

    @DisplayName("Получение шаблона визуализации по Id")
    @TmsLink("643644")
    @Test
    public void getVisualTemplateById() {
        String name = "get_by_id_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        GetImpl productCatalogGet = steps.getById(visualTemplates.getItemId(), GetVisualTemplateResponse.class);
        assertEquals(name, productCatalogGet.getName());
    }

    @DisplayName("Копирование шаблона визуализации по Id")
    @TmsLink("682886")
    @Test
    public void copyVisualTemplateById() {
        String name = "copy_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(true)
                .build()
                .createObject();
        String cloneName = visualTemplates.getName() + "-clone";
        steps.copyById(visualTemplates.getItemId());
        String cloneId = steps.getProductObjectIdByNameWithMultiSearch(cloneName, GetVisualTemplateListResponse.class);
        boolean isActive = steps.getJsonPath(cloneId).get("is_active");
        assertFalse(isActive);
        steps.deleteByName(cloneName, GetVisualTemplateListResponse.class);
        Assertions.assertFalse(steps.isExists(cloneName));
        steps.partialUpdateObject(visualTemplates.getItemId(), new JSONObject().put("is_active", false));
    }

    @DisplayName("Экспорт шаблона визуализации по Id")
    @TmsLink("643667")
    @Test
    public void exportVisualTemplateById() {
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name("export_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        steps.exportById(visualTemplates.getItemId());
    }

    @DisplayName("Частичное обновление шаблона визуализации по Id")
    @TmsLink("643668")
    @Test
    public void partialUpdateVisualTemplate() {
        String name = "partial_update_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        String expectedDescription = "UpdateDescription";
        steps.partialUpdateObject(visualTemplates.getItemId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetImpl getResponse = steps.getById(visualTemplates.getItemId(), GetVisualTemplateResponse.class);
        String actualDescription = getResponse.getDescription();
        assertEquals(expectedDescription, actualDescription);
    }

    @DisplayName("Получение шаблона визуализации по event_provider, event_type")
    @TmsLink("643671")
    @Test
    public void getVisualTemplateByProviderAndType() {
        String name = "get_by_provider_type_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        GetVisualTemplateResponse visualTemplate = steps
                .getItemVisualTemplate(visualTemplates.getEventType().get(0), visualTemplates.getEventProvider().get(0));
        assertEquals(visualTemplate.getEventProvider(), visualTemplate.getEventProvider());
        assertEquals(visualTemplate.getEventType(), visualTemplate.getEventType());
    }

    @DisplayName("Сортировка шаблонов визуализации по статусу")
    @TmsLink("")
    @Test
    public void orderingByStatus() {
        List<ItemImpl> list = steps.orderingByStatus(GetVisualTemplateListResponse.class).getItemsList();
        boolean result = false;
        int count = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            ListItem item = (ListItem) list.get(i);
            ListItem nextItem = (ListItem) list.get(i + 1);
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
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
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

    @Order(99)
    @DisplayName("Проверка на наличие ключей в CompactTemplate")
    @TmsLink("742495")
    @Test
    public void compactTemplateFields() {
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
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
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name("delete_item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        steps.deleteById(visualTemplates.getItemId());
    }
}
