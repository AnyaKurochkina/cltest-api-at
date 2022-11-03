package api.t1.imageService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.Marketing;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.t1.imageService.ImageServiceSteps.*;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("marketing")
public class MarketingTest extends Tests {

    @Test
    @TmsLink("1258504")
    @DisplayName("Создание marketing")
    public void createMarketingTest() {
        Marketing marketing = Marketing.builder()
                .name("marketing_create_api_test")
                .build()
                .createObject();
    }

    @Test
    @TmsLink("1258511")
    @DisplayName("Получение списка marketing")
    public void getMarketingListTest() {
        Marketing marketing = Marketing.builder()
                .name("get_marketing_list_api_test")
                .build()
                .createObject();
        List<Marketing> marketingList = getMarketingList();
        assertFalse(marketingList.isEmpty(), "Список marketing пустой");
        assertTrue(isMarketingExist(marketing.getName()), String.format("Marketing с именем %s не найден", marketing.getName()));
    }

    @Test
    @TmsLink("1258548")
    @DisplayName("Частичное обновление marketing")
    public void partialUpdateMarketingListTest() {
        Marketing marketing = Marketing.builder()
                .name("partial_update_marketing_list_api_test")
                .build()
                .createObject();
        partialUpdateMarketingById(marketing.getId(), new JSONObject().put("description", "test"));

    }
}
