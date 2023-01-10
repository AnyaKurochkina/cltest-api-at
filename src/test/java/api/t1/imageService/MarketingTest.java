package api.t1.imageService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.imageService.Marketing;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.t1.imageService.ImageServiceSteps.*;
import static steps.t1.imageService.ImageServiceSteps.createMarketing;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("marketing")
public class MarketingTest extends Tests {

    @Test
    @TmsLink("1258504")
    @DisplayName("Создание marketing")
    public void createMarketingTest() {
        Marketing marketing = createMarketing("marketing_create_api_test");
        assertTrue(isMarketingExist(marketing.getName()));
    }

    @Test
    @TmsLink("1263334")
    @DisplayName("Получение marketing по id")
    public void getMarketingByIdTest() {
        Marketing marketing = createMarketing("get_marketing_by_id_api_test");
        Marketing marketingById = getMarketingById(marketing.getId());
        assertEquals(marketing, marketingById);
    }

    @Test
    @TmsLink("1258511")
    @DisplayName("Получение списка marketing")
    public void getMarketingListTest() {
        Marketing marketing = createMarketing("get_marketing_list_api_test");
        List<Marketing> marketingList = getMarketingList();
        assertFalse(marketingList.isEmpty(), "Список marketing пустой");
        assertTrue(isMarketingExist(marketing.getName()), String.format("Marketing с именем %s не найден", marketing.getName()));
    }

    @Test
    @TmsLink("1258548")
    @DisplayName("Частичное обновление marketing")
    public void partialUpdateMarketingListTest() {
        Marketing marketing = createMarketing("partial_update_marketing_list_api_test");
        partialUpdateMarketingById(marketing.getId(), new JSONObject().put("description", "test")
                .put("name", marketing.getName()));
        Marketing marketingById = getMarketingById(marketing.getId());
        assertEquals("test", marketingById.getDescription());
    }

    @Test
    @TmsLink("1263320")
    @DisplayName("Удаление marketing по id")
    public void deleteMarketingTest() {
        Marketing marketing = createMarketing("delete_marketing_api_test");
        deleteMarketingById(marketing.getId());
        assertFalse(isMarketingExist(marketing.getName()));
    }
}
