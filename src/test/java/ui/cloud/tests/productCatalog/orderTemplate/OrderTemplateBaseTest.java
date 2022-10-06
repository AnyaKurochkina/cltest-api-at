package ui.cloud.tests.productCatalog.orderTemplate;

import httpModels.productCatalog.itemVisualItem.createVisualTemplate.*;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.GetVisualTemplateListResponse;
import io.qameta.allure.Epic;
import models.productCatalog.ItemVisualTemplate;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Epic("Шаблоны отображения")
@DisabledIfEnv("prod")
public class OrderTemplateBaseTest extends BaseTest {
    final static String TITLE = "AT UI Template";
    final static String DESCRIPTION = "Description";
    private static final ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json");
    private static final CompactTemplate compactTemplate = CompactTemplate.builder().name(new Name("name"))
            .type(new Type("type")).status(new Status("status")).build();
    private static final FullTemplate fullTemplate = FullTemplate.builder().type("tab").value(Arrays.asList("testValue")).build();
    final String NAME = UUID.randomUUID().toString();
    ItemVisualTemplate orderTemplate;

    @BeforeEach
    public void setUp() {
        createOrderTemplate(NAME);
    }

    private void createOrderTemplate(String name) {
        orderTemplate = ItemVisualTemplate.builder()
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .eventType(Collections.singletonList("vm"))
                .eventProvider(Collections.singletonList("vsphere"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
    }

    void deleteOrderTemplate(String name) {
        steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch(name, GetVisualTemplateListResponse.class)).assertStatus(204);
    }
}
