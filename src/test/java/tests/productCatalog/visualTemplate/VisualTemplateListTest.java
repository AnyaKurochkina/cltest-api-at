package tests.productCatalog.visualTemplate;

import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.*;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.GetVisualTemplateListResponse;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.ItemVisualTemplates;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json");
    CompactTemplate compactTemplate = CompactTemplate.builder().name(new Name("name"))
            .type(new Type("type")).status(new Status("status")).build();
    FullTemplate fullTemplate = FullTemplate.builder().type("type").value(Arrays.asList("value", "value2")).build();

    @DisplayName("Получение списка шаблонов визуализаций")
    @TmsLink("643632")
    @Test
    public void getVisualTemplateList() {
        List<ItemImpl> list = steps.getProductObjectList(GetVisualTemplateListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка шаблонов визуализаций")
    @TmsLink("682862")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetVisualTemplateListResponse.class).getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка шаблонов визуализаций по фильтру event_provider")
    @TmsLink("643634")
    @Test
    public void getVisualTemplateListByProvider() {
        String name = "get_list_by_event_provider_item_visual_template_test_api";
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
        List<ItemImpl> providerList = steps.getProductObjectList(GetVisualTemplateListResponse.class,
                "?event_provider=" + providerFilter);
        assertTrue(providerList.size() > 0);
        for (ItemImpl impl : providerList) {
            assertTrue(steps.getJsonPath(impl.getId()).getString("event_provider").contains(providerFilter));
        }
    }

    @DisplayName("Получение списка шаблонов визуализаций по фильтру event_type")
    @TmsLink("643636")
    @Test
    public void getVisualTemplateListByType() {
        String name = "get_list_by_event_type_item_visual_template_test_api";
        ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder()
                .name(name)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        String typeFilter = visualTemplates.getEventType().get(0);
        List<ItemImpl> typeList = steps.getProductObjectList(GetVisualTemplateListResponse.class,
                "?event_type=" + typeFilter);
        assertTrue(typeList.size() > 0);
        for (ItemImpl impl : typeList) {
            assertTrue(steps.getJsonPath(impl.getId()).getString("event_type").contains(typeFilter));
        }
    }

    @DisplayName("Получение списка шаблонов визуализации по is_active")
    @TmsLink("823845")
    @Test
    public void getVisualTemplateListByIsActive() {
        ItemVisualTemplates.builder().name("get_visual_template_list_by_is_active")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        List<ItemImpl> list = steps.getProductObjectList(GetVisualTemplateListResponse.class,
                "?is_active=false");
        for (ItemImpl item : list) {
            ListItem listItem = (ListItem) item;
            assertFalse(listItem.getIsActive());
        }
    }

    @DisplayName("Получение списка шаблонов визуализации по провайдеру")
    @TmsLink("823850")
    @Test
    public void getVisualTemplateListByEventProvider() {
        ItemVisualTemplates.builder().name("get_visual_template_list_by_provider")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        List<ItemImpl> list = steps.getProductObjectList(GetVisualTemplateListResponse.class, "?event_provider=docker");
        for (ItemImpl item : list) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getEventProvider().contains("docker"));
        }
    }

    @DisplayName("Получение списка шаблонов визуализации по типу")
    @TmsLink("823851")
    @Test
    public void getVisualTemplateListByEventType() {
        List<String> eventType = Collections.singletonList("app");
        ItemVisualTemplates.builder().name("get_visual_template_list_by_type")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(eventType)
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
        List<ItemImpl> list = steps.getProductObjectList(GetVisualTemplateListResponse.class, "?event_type=app");
        for (ItemImpl item : list) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getEventType().contains("app"));
        }
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
}
