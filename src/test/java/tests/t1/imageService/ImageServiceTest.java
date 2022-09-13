package tests.t1.imageService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.ImageGroups;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static steps.t1.imageService.ImageServiceSteps.*;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("image_groups")
public class ImageServiceTest extends Tests {

    @Test
    @TmsLink("1151854")
    @DisplayName("Получение списка ImageGroups")
    public void getImageGroupsListTest() {
        ImageGroups imageGroup = ImageGroups.builder()
                .name("get_image_groups_test_api")
                .tags(Collections.singletonList("os:linux"))
                .distro("fedora")
                .build()
                .createObject();
        assertTrue(isImageGroupExist(imageGroup.getName()));
    }

    @Test
    @TmsLink("1152127")
    @DisplayName("Создание ImageGroups")
    public void createImageGroupTest() {
        ImageGroups imageGroup = ImageGroups.builder()
                .name("create_image_groups_test_api")
                .tags(Collections.singletonList("os:linux"))
                .distro("fedora")
                .build()
                .createObject();
        ImageGroups actualImageGroup = getImageGroup(imageGroup.getId());
        assertEquals(imageGroup, actualImageGroup);
    }

    @Test
    @TmsLink("1152154")
    @DisplayName("Удаление ImageGroups")
    public void deleteImageGroupTest() {
        JSONObject jsonObject = ImageGroups.builder()
                .name("delete_image_groups_test_api")
                .tags(Collections.singletonList("os:linux"))
                .distro("fedora")
                .build()
                .init()
                .toJson();
        ImageGroups imageGroup = createImageGroup(jsonObject);
        deleteImageGroupById(imageGroup.getId());
        assertFalse(isImageGroupExist(imageGroup.getName()));
    }
}
