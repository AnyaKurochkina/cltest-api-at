package api.t1.imageService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.imageService.Categories;
import org.junit.jupiter.api.*;
import steps.t1.imageService.ImageServiceSteps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.t1.imageService.ImageServiceSteps.*;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("Categories")
public class CategoriesTest extends Tests {
    private static final List<String> categoriesId = new ArrayList<>();

    @AfterAll
    public static void deleteTestData() {
        categoriesId.forEach(ImageServiceSteps::deleteCategoryById);
    }

    @Test
    @TmsLink("1601233")
    @DisplayName("Получение списка категорий сервиса образов")
    public void getCategoriesListTest() {
        Categories categories = createCategories("get_list_categories_test_api");
        categoriesId.add(categories.getId());
        assertTrue(isCategoryExist(categories.getId()), "Категория отсутствует в списке");
    }

    @Test
    @TmsLink("1601239")
    @DisplayName("Создание категории сервиса образов")
    public void createCategoryTest() {
        String name = "create_category_test_api";
        Categories categories = createCategories(name);
        categoriesId.add(categories.getId());
        assertEquals(name, categories.getName());
    }

    @Test
    @TmsLink("1601241")
    @DisplayName("Обновление категории сервиса образов")
    public void updateCategoryTest() {
        Categories categories = createCategories("update_category_test_api");
        categoriesId.add(categories.getId());
        String name = "update_category_test_api";
        updateCategoryById(categories.getId(), "updated_category_name_test_api");
        assertEquals(name, categories.getName());
    }

    @Test
    @TmsLink("1601245")
    @DisplayName("Получение категории по id")
    public void getCategoryByIdTest() {
        Categories categories = createCategories("get_category_by_id_test_api");
        categoriesId.add(categories.getId());
        Categories actualCategory = getCategoryById(categories.getId());
        assertEquals(categories, actualCategory);
    }

    @Test
    @TmsLink("1601247")
    @DisplayName("Удаление категории по id")
    public void deleteCategoryByIdTest() {
        Categories categories = createCategories("delete_category_by_id_test_api");
        deleteCategoryById(categories.getId());
        assertFalse(isCategoryExist(categories.getId()));
    }

    @Test
    @Disabled
    @TmsLink("1601251")
    @DisplayName("Негативный тест на создание категории с уже существующим именем")
    public void createCategoryWithExistNameTest() {
        String name = "create_category_with_exist_name_test_api";
        Categories categories = createCategories(name);
        categoriesId.add(categories.getId());
        createCategoriesResponse(name).assertStatus(400);
        //todo Когда исправят баг доделать тест
    }
}
