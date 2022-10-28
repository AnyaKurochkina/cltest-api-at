package tests.t1.imageService;

import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.Image;
import models.t1.ImageGroups;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.t1.imageService.ImageServiceSteps.*;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("image_groups")
public class ImageServiceTest extends Tests {

    @Test
    @TmsLink("1151854")
    @DisplayName("Получение списка ImageGroups c флагом need_all=false")
    public void getImageGroupsListTest() {
        ImageGroups imageGroup = ImageGroups.builder()
                .name("get_image_groups_test_api")
                .tags(Collections.singletonList("type:os"))
                .distro("altsisyphus")
                .build()
                .createObject();
        Waiting.sleep(15000);
        assertTrue(isImageGroupExist(imageGroup.getName(), false), String.format("Группы с именем %s нет в списке", imageGroup.getName()));
    }

    @Test
    @TmsLink("1175215")
    @DisplayName("Получение списка ImageGroups c флагом need_all=true")
    public void getImageGroupsListNeedAllTrueTest() {
        ImageGroups imageGroup = ImageGroups.builder()
                .name("get_image_groups_need_all_true_test_api")
                .tags(Collections.singletonList("os:need_all_true"))
                .distro("fedora")
                .build()
                .createObject();
        assertTrue(isImageGroupExist(imageGroup.getName(), true), String.format("Группы с именем %s нет в списке", imageGroup.getName()));
    }

    @Test
    @TmsLink("1172500")
    @DisplayName("Получение списка ImageGroups по региону")
    public void getImageGroupsListByRegionTest() {
        String region = "portal-dcb";
        List<ImageGroups> imageGroupsList = getImageGroupsListByRegion(region);
        for (ImageGroups groups : imageGroupsList) {
            List<Image> imageList = groups.getImages();
            for (Image image : imageList) {
                assertEquals(region, image.getAvailabilityZone(), "Регионы не совпадают");
            }
        }
    }

    @Test
    @TmsLink("1172516")
    @DisplayName("Получение списка ImageGroups по несуществующему региону")
    public void getImageGroupsListByNotExistRegionTest() {
        String region = "no-exist";
        List<ImageGroups> imageGroupsList = getImageGroupsListByRegion(region);
        assertTrue(imageGroupsList.isEmpty(), "Список не пустой");
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
                .tags(Collections.singletonList("os:delete_test"))
                .distro("fedora")
                .build()
                .init()
                .toJson();
        ImageGroups imageGroup = createImageGroup(jsonObject);
        deleteImageGroupById(imageGroup.getId());
        assertFalse(isImageGroupExist(imageGroup.getName(), true), "ImageGroup не удалился.");
    }

    @Test
    @TmsLink("1175218")
    @DisplayName("Обновление ImageGroups")
    public void patchImageGroupTest() {
        ImageGroups imageGroups = ImageGroups.builder()
                .name("partial_update_image_groups_test_api")
                .tags(Arrays.asList("test", "test2"))
                .distro("distro_test")
                .build()
                .createObject();
        ImageGroups updatedImageGroups = ImageGroups.builder()
                .name("partial_update_image_groups_test_api")
                .tags(Arrays.asList("tag_update"))
                .distro("distro_update")
                .build();
        JSONObject jsonObject = updatedImageGroups.init().toJson();
        partialUpdateImageGroupById(imageGroups.getId(), jsonObject);
        assertEquals(getImageGroup(imageGroups.getId()).getTags(), updatedImageGroups.getTags());
    }
}
