package steps.t1.imageService;

import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.ImageGroups;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.enums.Role.T1_ADMIN;
import static core.helper.Configure.ImageService;

public class ImageServiceSteps extends Steps {

    @Step("Полуение списка image groups")
    public static List<ImageGroups> getImageGroupsList() {
        return new Http(ImageService)
                .setRole(T1_ADMIN)
                .get("/image_groups")
                .assertStatus(200)
                .jsonPath()
                .getList("", ImageGroups.class);
    }

    @Step("Получение image groups по id {id}")
    public static ImageGroups getImageGroup(String id) {
        return new Http(ImageService)
                .setRole(T1_ADMIN)
                .get("/image_groups/{}", id)
                .assertStatus(200)
                .extractAs(ImageGroups.class);
    }

    @Step("Удаление image groups по id {id}")
    public static void deleteImageGroupById(String id) {
        new Http(ImageService)
                .setRole(T1_ADMIN)
                .delete("/image_groups/{}", id)
                .assertStatus(200);
    }

    @Step("Создание image groups")
    public static ImageGroups createImageGroup(JSONObject object) {
        return new Http(ImageService)
                .setRole(T1_ADMIN)
                .body(object)
                .post("/image_groups")
                .assertStatus(200)
                .extractAs(ImageGroups.class);
    }

    @Step("Проверка существования image group c именем {name}")
    public static boolean isImageGroupExist(String name) {
        List<ImageGroups> imageGroupList = getImageGroupsList();
        for (ImageGroups imageGroup : imageGroupList) {
            if (imageGroup.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
