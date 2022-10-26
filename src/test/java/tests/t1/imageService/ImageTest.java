package tests.t1.imageService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.t1.imageService.ImageServiceSteps.getImageById;
import static steps.t1.imageService.ImageServiceSteps.getImageList;

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
    @TmsLink("")
    @DisplayName("Получение Image по id")
    public void getImageByIdTest() {
        List<Image> imageList = getImageList();
        getImageById(imageList.get(0).getInternalId());
    }

}
