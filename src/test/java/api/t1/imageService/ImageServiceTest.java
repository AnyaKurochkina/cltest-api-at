package api.t1.imageService;

import api.Tests;
import core.helper.http.Response;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.imageService.Image;
import models.t1.imageService.ImageGroup;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
        ImageGroup imageGroup = ImageGroup.builder()
                .name("get_image_groups_test_api")
                .tags(Collections.singletonList("type:os"))
                .distro("altsisyphus")
                .build()
                .createObject();
        Waiting.sleep(15000);
        assertFalse(isImageGroupExist(imageGroup.getName(), false), String.format("Группы с именем %s нет в списке", imageGroup.getName()));
    }

    @Test
    @TmsLink("1175215")
    @DisplayName("Получение списка ImageGroups c флагом need_all=true")
    public void getImageGroupsListNeedAllTrueTest() {
        ImageGroup imageGroup = ImageGroup.builder()
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
        List<ImageGroup> imageGroupsList = getImageGroupsListByRegion(region);
        for (ImageGroup groups : imageGroupsList) {
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
        List<ImageGroup> imageGroupsList = getImageGroupsListByRegion(region);
        assertTrue(imageGroupsList.isEmpty(), "Список не пустой");
    }

    @Test
    @TmsLink("1152127")
    @DisplayName("Создание ImageGroups")
    public void createImageGroupTest() {
        ImageGroup imageGroup = ImageGroup.builder()
                .name("create_image_groups_test_api")
                .tags(Collections.singletonList("os:linux"))
                .distro("fedora")
                .build()
                .createObject();
        ImageGroup actualImageGroup = getImageGroup(imageGroup.getId());
        assertEquals(imageGroup, actualImageGroup);
    }

    @Test
    @TmsLink("1152154")
    @DisplayName("Удаление ImageGroups")
    public void deleteImageGroupTest() {
        String groupName = "delete_image_groups_test_api";
        if (isImageGroupExist(groupName, true)) {
            deleteImageGroupByName(groupName);
        }
        JSONObject jsonObject = ImageGroup.builder()
                .name(groupName)
                .tags(Collections.singletonList("os:delete_test"))
                .distro("fedora")
                .build()
                .init()
                .toJson();
        ImageGroup imageGroup = createImageGroup(jsonObject);
        deleteImageGroupById(imageGroup.getId());
        assertFalse(isImageGroupExist(imageGroup.getName(), true), "ImageGroup не удалился.");
    }

    @Test
    @TmsLink("1175218")
    @DisplayName("Обновление ImageGroups")
    public void patchImageGroupTest() {
        ImageGroup imageGroup = ImageGroup.builder()
                .name("partial_update_image_groups_test_api")
                .tags(Arrays.asList("test", "test2"))
                .distro("distro_test")
                .build()
                .createObject();
        ImageGroup updatedImageGroup = ImageGroup.builder()
                .name("partial_update_image_groups_test_api")
                .tags(Collections.singletonList("tag_update"))
                .distro("distro_update")
                .build();
        JSONObject jsonObject = updatedImageGroup.init().toJson();
        partialUpdateImageGroupById(imageGroup.getId(), jsonObject);
        assertEquals(getImageGroup(imageGroup.getId()).getTags(), updatedImageGroup.getTags());
    }

    @DisplayName("Получение версии сервиса образов")
    @TmsLink("1327691")
    @Test
    public void getImageServiceVersionTest() {
        Response resp = getImageServiceVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
        assertNotNull(resp.jsonPath().get("stage"));
    }

    @DisplayName("Получение статуса health")
    @TmsLink("1327717")
    @Test
    public void healthImageServiceTest() {
        assertEquals("ok", getHealthStatusImageService());
    }
}
