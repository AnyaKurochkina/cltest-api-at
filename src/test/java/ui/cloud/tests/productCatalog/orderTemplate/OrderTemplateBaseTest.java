package ui.cloud.tests.productCatalog.orderTemplate;

import httpModels.productCatalog.itemVisualItem.createVisualTemplate.*;
import models.productCatalog.ItemVisualTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class OrderTemplateBaseTest extends BaseTest {
    final static String TITLE = "AT UI Template";
    final static String DESCRIPTION = "Description";
    private static final ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json");
    final String NAME = UUID.randomUUID().toString();
    ItemVisualTemplate orderTemplate;
    private static CompactTemplate compactTemplate = CompactTemplate.builder().name(new Name("name"))
            .type(new Type("type")).status(new Status("status")).build();
    private static FullTemplate fullTemplate = FullTemplate.builder().type("tab").value(Arrays.asList("testValue")).build();

    @BeforeEach
    public void setUp() {
        createOrderTemplate(NAME);
    }

    @AfterEach
    public void tearDown() {
        deleteOrderTemplate(NAME);
    }

    private void createOrderTemplate(String name) {
        orderTemplate = ItemVisualTemplate.builder()
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .eventProvider(Collections.singletonList("vm"))
                .eventType(Collections.singletonList("vsphere"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .build()
                .createObject();
    }

    private void deleteOrderTemplate(String name) {
        /*steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch(name, GetVisualTemplateListResponse.class)).assertStatus(204);*/
    }
}
