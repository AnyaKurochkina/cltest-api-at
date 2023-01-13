package api.t1.imageService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.imageService.Image;
import models.t1.imageService.Marketing;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.t1.imageService.ImageServiceSteps.*;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("image")
public class ImageTest {

    @Test
    @TmsLink("1258589")
    @DisplayName("Получение списка Image")
    public void getImageListTest() {
        List<Image> imageList = getImageList();
        assertFalse(imageList.isEmpty());
    }

    @Test
    @TmsLink("1258620")
    @DisplayName("Получение Image по id")
    public void getImageByIdTest() {
        List<Image> imageList = getImageList();
        getImageById(imageList.get(0).getInternalId());
    }

    @Test
    @TmsLink("1263406")
    @DisplayName("Обновление marketing у Image")
    public void updateImageMarketingInfoTest() {
        Marketing expectedMarketing = Marketing.builder()
                .name("marketing_for_image_test_api")
                .description("test_api")
                .build()
                .createObject();
        Image ubuntu = getImageByName("ubuntu");
        partialUpdateImageById(Objects.requireNonNull(ubuntu).getInternalId(), new JSONObject().put("marketing_info_id", expectedMarketing.getId()));
        Image imageById = getImageById(ubuntu.getInternalId());
        Marketing actualMarketing = imageById.getMarketing();
        assertEquals(expectedMarketing, actualMarketing);
    }

}
