package api.cloud.productCatalog.productCard;

import core.helper.JsonHelper;
import core.helper.StringUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.productCard.CardItems;
import models.cloud.productCatalog.productCard.ProductCard;
import models.cloud.productCatalog.service.Service;
import models.cloud.productCatalog.template.Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.StringUtils.convertStringVersionToIntArrayVersion;
import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.GraphSteps.deleteGraphById;
import static steps.productCatalog.ProductCardSteps.*;
import static steps.productCatalog.ProductSteps.*;
import static steps.productCatalog.ServiceSteps.createService;
import static steps.productCatalog.TemplateSteps.createTemplateByName;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продуктовые карты")
@DisabledIfEnv("prod")
public class ProductCardsTest {

    @Test
    @DisplayName("Создание продуктовой карты без card items")
    @TmsLink("SOUL-7347")
    public void createProductCardWithOutCardItemsTest() {
        ProductCard productCard = ProductCard.builder()
                .name("create_product_card_with_out_test_api")
                .title("create_product_card_with_out_test_api")
                .description("test_api")
                .cardItems(Collections.emptyList())
                .build()
                .createObject();
        ProductCard getProductCard = getProductCard(productCard.getId());
        assertEquals(productCard, getProductCard);
    }

    @Test
    @DisplayName("Создание продуктовой карты с card items")
    @TmsLink("SOUL-7348")
    public void createProductCardWithCardItemsTest() {
        Action action = createAction();
        Product product = createProduct("product_for_card_items_test_api");
        CardItems actionCard = CardItems.builder().objType("Action")
                .objId(action.getActionId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        CardItems productCardItem = CardItems.builder().objType("Product")
                .objId(product.getProductId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();

        List<CardItems> cardItemsList = new ArrayList<>();
        cardItemsList.add(actionCard);
        cardItemsList.add(productCardItem);

        ProductCard productCard = ProductCard.builder()
                .name("create_product_card_with_card_items_test_api")
                .title("create_product_card_with_card_items_title_test_api")
                .description("test_api")
                .cardItems(cardItemsList)
                .build()
                .createObject();
        ProductCard getProductCard = getProductCard(productCard.getId());
        for (CardItems items : getProductCard.getCardItems()) {
            if (items.getObjType().equals("Action")) {
                assertEquals(action.getName(), items.getObjKeys().getName());
            }
            if (items.getObjType().equals("Product")) {
                assertEquals(product.getName(), items.getObjKeys().getName());
            }
        }
        assertEquals(2, getProductCard.getCardItems().size());
    }

    @DisplayName("Проверка существования продуктовой карты по имени")
    @Test
    @TmsLink("SOUL-7349")
    public void checkProductCardExistsTest() {
        String productName = "product_card_exist_test_api";
        ProductCard.builder()
                .name(productName)
                .build()
                .createObject();
        assertTrue(isProductCardExists(productName));
        assertFalse(isProductCardExists("not_exists_name"));
    }

    @DisplayName("Копирование продуктовой карты")
    @Test
    @TmsLink("SOUL-7692")
    public void copyProductCardTest() {
        String productName = "copy_product_card_test_api";
        Template template = createTemplateByName("template_for_copy_product_card_test_api");
        CardItems templateCardItem = CardItems.builder().objType("Template")
                .objId(String.valueOf(template.getId()))
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        ProductCard productCard = ProductCard.builder()
                .name(productName)
                .cardItems(Collections.singletonList(templateCardItem))
                .build()
                .createObject();
        ProductCard copiedProductCard = copyProductCard(productCard.getId());
        deleteProductCard(copiedProductCard.getId());
        assertEquals(productCard.getName() + "-clone", copiedProductCard.getName());
    }

    @DisplayName("Применение продуктовой карты.")
    @Test
    @TmsLink("SOUL-7693")
    public void applyProductCardTest() {
        Graph graphForAction = createGraph();
        Graph graphForProduct = createGraph();
        JSONObject actionJson = Action.builder()
                .name("action_for_apply_card_items_test_api")
                .graphId(graphForAction.getGraphId())
                .version("1.0.2")
                .build()
                .init()
                .toJson();
        Action action = createAction(actionJson).assertStatus(201).extractAs(Action.class);
        JSONObject productJson = Product.builder()
                .name("product_for_apply_card_items_test_api")
                .graphId(graphForProduct.getGraphId())
                .version("2.0.0")
                .build()
                .init()
                .toJson();
        Product product = createProduct(productJson);
        CardItems actionCard = CardItems.builder().objType("Action")
                .objId(action.getActionId())
                .versionArr(Arrays.asList(1, 0, 2))
                .build();
        CardItems productCardItem = CardItems.builder().objType("Product")
                .objId(product.getProductId())
                .versionArr(Arrays.asList(2, 0, 0))
                .build();

        List<CardItems> cardItemsList = new ArrayList<>();
        cardItemsList.add(actionCard);
        cardItemsList.add(productCardItem);

        ProductCard productCard = ProductCard.builder()
                .name("apply_product_card_test_api")
                .title("apply_product_card_title_test_api")
                .description("test_api")
                .cardItems(cardItemsList)
                .build()
                .createObject();
        deleteActionById(action.getActionId());
        deleteProductById(product.getProductId());
        applyProductCard(productCard.getId());
        Product productByName = getProductByName(product.getName());
        Action actionByName = getActionByName(action.getName());
        product.setCurrentVersion("2.0.0");
        action.setCurrentVersion("1.0.2");
        assertEquals(product, productByName);
        assertEquals(action, actionByName);
        deleteProductByName(product.getName());
        deleteActionByName(action.getName());
    }

    @DisplayName("Применение продуктовой карты объектов версии которых не совпадают.")
    @Test
    @TmsLink("SOUL-8693")
    public void applyProductCardObjectVersionsNotEqualsTest() {
        String actionName = "action_for_apply_card_items_not_equals_objects_test_api";
        String actionVersion = "1.0.2";
        try {
            Graph graphForAction = createGraph();
            JSONObject actionJson = Action.builder()
                    .name(actionName)
                    .graphId(graphForAction.getGraphId())
                    .version(actionVersion)
                    .build()
                    .init()
                    .toJson();
            Action action = createAction(actionJson).assertStatus(201).extractAs(Action.class);
            CardItems actionCard = CardItems.builder().objType("Action")
                    .objId(action.getActionId())
                    .versionArr(convertStringVersionToIntArrayVersion(actionVersion))
                    .build();

            ProductCard productCard = ProductCard.builder()
                    .name("apply_product_card_test_api")
                    .title("apply_product_card_title_test_api")
                    .description("test_api")
                    .cardItems(Collections.singletonList(actionCard))
                    .build()
                    .createObject();
            partialUpdateAction(action.getActionId(), new JSONObject().put("type", "on"));

            ImportObject object = applyProductCard(productCard.getId()).jsonPath().getList("imported_objects", ImportObject.class)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Список Imported objects пустой."));
            assertAll(
                    () -> assertEquals(actionName, object.getObjectName(), "Имя объекта в ответе после применения продуктовой карты не соответствует ожидаемому"),
                    () -> assertEquals(action.getActionId(), object.getObjectId(), "Id объекта в ответе после применения продуктовой карты не соответствует ожидаемому"),
                    () -> assertEquals(format("Error loading dump: Версия \"{}\" Action:{} уже существует, но с другим наполнением. Измените значение версии (\"version_arr: {}\") у импортируемого объекта и попробуйте снова.", actionVersion, actionName, convertStringVersionToIntArrayVersion(actionVersion))
                            , object.getMessages().get(0))
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteActionByName(actionName);
        }
    }

    @DisplayName("Добавление в карту продукта версионный объект, который уже есть в карте, но с другой версией.")
    @Test
    @TmsLink("SOUL-8690")
    public void addAlreadyExistObjectToProductCardWithOtherVersionTest() {
        String productName = StringUtils.getRandomStringApi(7);
        try {
            List<CardItems> cardItemsList = new ArrayList<>();
            Graph graphForProduct = createGraph();
            JSONObject productJson = Product.builder()
                    .name(productName)
                    .graphId(graphForProduct.getGraphId())
                    .version("2.0.0")
                    .build()
                    .init()
                    .toJson();
            Product product = createProduct(productJson);
            CardItems productCardItem = CardItems.builder().objType("Product")
                    .objId(product.getProductId())
                    .versionArr(Arrays.asList(2, 0, 0))
                    .build();

            partialUpdateProduct(product.getProductId(), new JSONObject().put("max_count", 9));
            CardItems productCardItem2 = CardItems.builder().objType("Product")
                    .objId(product.getProductId())
                    .versionArr(Arrays.asList(2, 0, 1))
                    .build();

            cardItemsList.add(productCardItem);
            cardItemsList.add(productCardItem2);

            JSONObject json = ProductCard.builder()
                    .name("apply_product_card_test_api")
                    .title("apply_product_card_title_test_api")
                    .description("test_api")
                    .cardItems(cardItemsList)
                    .build()
                    .init()
                    .toJson();
            String errorMessage = uncheckedCreateProductCard(json).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
            assertEquals("There are no unique elements in card_items", errorMessage, "Сообщение об ошибке при создании " +
                    "product card с не уникальными элементами не соответсвует формату");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteProductByName(productName);
        }
    }


    @DisplayName("Обновление продуктовой карты")
    @Test
    @TmsLink("SOUL-7694")
    public void updateProductCardTest() {
        String productName = "product_card_update_test_api";
        ProductCard productCard = ProductCard.builder()
                .name(productName)
                .build()
                .createObject();
        Graph graph = createGraph();
        CardItems productCardItem = CardItems.builder().objType("Graph")
                .objId(graph.getGraphId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        JSONObject json = ProductCard.builder()
                .name(productName)
                .cardItems(Collections.singletonList(productCardItem))
                .build()
                .toJson();
        ProductCard updatedProductCard = updateProductCard(productCard.getId(), json).assertStatus(200).extractAs(ProductCard.class);
        CardItems actualProductCardItem = updatedProductCard.getCardItems().get(0);
        assertEquals("Graph", actualProductCardItem.getObjType());
        assertEquals(graph.getName(), actualProductCardItem.getObjKeys().getName());
        assertEquals(productCardItem.getVersionArr(), actualProductCardItem.getVersionArr());
    }

    @SneakyThrows
    @DisplayName("Частичное обновление продуктовой карты")
    @Test
    @TmsLink("SOUL-7695")
    public void partialUpdateProductCardTest() {
        Graph graph = createGraph();
        CardItems productCardGraph = CardItems.builder().objType("Graph")
                .objId(graph.getGraphId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        String productName = "product_card_partial_update_test_api";
        ProductCard productCard = ProductCard.builder()
                .name(productName)
                .cardItems(Collections.singletonList(productCardGraph))
                .build()
                .createObject();
        Service service = createService(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        CardItems productCardService = CardItems.builder().objType("Service")
                .objId(service.getId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        ProductCard updatedProductCard = partialUpdateProductCard(productCard.getId(), new JSONObject()
                .put("card_items", new JSONArray(JsonHelper.getCustomObjectMapper().writeValueAsString(Collections.singletonList(productCardService)))))
                .assertStatus(200).extractAs(ProductCard.class);
        CardItems actualProductCardItem = updatedProductCard.getCardItems().get(0);
        assertEquals("Service", actualProductCardItem.getObjType());
        assertEquals(service.getName(), actualProductCardItem.getObjKeys().getName());
        assertEquals(productCardService.getVersionArr(), actualProductCardItem.getVersionArr());
    }

    @DisplayName("Проверка существования card items = false")
    @Test
    @TmsLink("SOUL-7696")
    public void isCardItemExistFalseTest() {
        JSONObject json = Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api")
                .build()
                .init()
                .toJson();
        Graph graph = createGraph(json).extractAs(Graph.class);
        CardItems cardItem = createCardItem("Graph", graph.getGraphId(), "1.0.0");
        ProductCard productCard = createProductCard("is_object_exist_false_test_api", cardItem);
        deleteGraphById(graph.getGraphId());
        CardItems getCardItems = getProductCard(productCard.getId()).getCardItems().get(0);
        assertFalse(getCardItems.getIsObjExists(), "Поле is_obj_exists = true");
        assertFalse(getCardItems.getIsObjVersionExists() && getCardItems.getIsObjEqual(),
                "Одно из полей is_obj_version_exists, is_obj_equal = true");
    }

    @DisplayName("Проверка существования card items = true")
    @Test
    @TmsLink("SOUL-7697")
    public void isCardItemExistTrueTest() {
        JSONObject json = Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api")
                .build()
                .init()
                .toJson();
        Graph graph = createGraph(json).extractAs(Graph.class);
        CardItems cardItem = createCardItem("Graph", graph.getGraphId(), "1.0.0");
        ProductCard productCard = createProductCard("is_object_exist_test_true_api", cardItem);
        deleteGraphById(graph.getGraphId());
        createGraph(json).extractAs(Graph.class);
        CardItems getCardItems = getProductCard(productCard.getId()).getCardItems().get(0);
        assertTrue(getCardItems.getIsObjExists(), "Поле is_obj_exists = false");
        assertTrue(getCardItems.getIsObjVersionExists() && getCardItems.getIsObjEqual(),
                "Одно из полей is_obj_version_exists, is_obj_equal = false");
    }

    @DisplayName("Проверка существования version у card items")
    @Test
    @TmsLink("SOUL-7698")
    public void isCardItemVersionExistTest() {
        String graphName = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api";
        JSONObject json = Graph.builder()
                .name(graphName)
                .version("2.0.0")
                .build()
                .init()
                .toJson();
        Graph graph = createGraph(json).extractAs(Graph.class);
        CardItems cardItem = createCardItem("Graph", graph.getGraphId(), "2.0.0");
        ProductCard productCard = createProductCard("is_object_version_exist_test_api", cardItem);
        deleteGraphById(graph.getGraphId());
        createGraph(graphName);
        assertFalse(getProductCard(productCard.getId()).getCardItems().get(0).getIsObjVersionExists(), "Поле is_obj_version_exists = true");
    }

    @DisplayName("Проверка существования разницы card items")
    @Test
    @TmsLink("SOUL-7699")
    public void isCardItemEqualsTest() {
        String graphName = RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api";
        Graph graph = createGraph(graphName);
        CardItems cardItem = createCardItem("Graph", graph.getGraphId(), "1.0.0");
        ProductCard productCard = createProductCard("is_object_equals_test_api", cardItem);
        deleteGraphById(graph.getGraphId());
        graph.setTitle("title_title");
        Graph createdGraph = createGraph(graph.toJson()).extractAs(Graph.class);
        CardItems getCardItems = getProductCard(productCard.getId()).getCardItems().get(0);
        assertFalse(getCardItems.getIsObjEqual(), "Поле is_obj_equals = true");
        assertTrue(getCardItems.getIsObjVersionExists() && getCardItems.getIsObjExists(),
                "Одно из полей is_obj_version_exists, is_obj_equal = false");
        deleteGraphById(createdGraph.getGraphId());
    }
}
