package steps.t1.imageService;

import core.helper.http.Http;
import io.qameta.allure.Step;
import models.t1.Image;
import models.t1.ImageGroups;
import models.t1.Marketing;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.enums.Role.CLOUD_ADMIN;
import static core.helper.Configure.ImageService;

public class ImageServiceSteps extends Steps {

    @Step("Полуение списка image groups")
    public static List<ImageGroups> getImageGroupsList(boolean isNeedAll) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/image_groups?need_all={}", isNeedAll)
                .assertStatus(200)
                .jsonPath()
                .getList("", ImageGroups.class);
    }

    @Step("Полуение списка marketing")
    public static List<Marketing> getMarketingList() {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/marketing")
                .assertStatus(200)
                .jsonPath()
                .getList("", Marketing.class);
    }

    @Step("Полуение списка image")
    public static List<Image> getImageList() {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/images")
                .assertStatus(200)
                .jsonPath()
                .getList("", Image.class);
    }

    @Step("Полуение списка image groups по region {region}")
    public static List<ImageGroups> getImageGroupsListByRegion(String region) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/image_groups?availability_zone={}", region)
                .assertStatus(200)
                .jsonPath()
                .getList("", ImageGroups.class);
    }

    @Step("Получение image groups по id {id}")
    public static ImageGroups getImageGroup(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/image_groups/{}", id)
                .assertStatus(200)
                .extractAs(ImageGroups.class);
    }

    @Step("Получение marketing по id {id}")
    public static Marketing getMarketingById(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/marketing/{}", id)
                .assertStatus(200)
                .extractAs(Marketing.class);
    }

    @Step("Получение marketing по name {name}")
    public static Marketing getMarketingByName(String name) {
        List<Marketing> marketingList = getMarketingList();
        for (Marketing marketing : marketingList) {
            if (marketing.getName().equals(name)) {
                return marketing;
            }
        }
        return null;
    }

    @Step("Получение image по id {id}")
    public static Image getImageById(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get("/images/{}", id)
                .assertStatus(200)
                .extractAs(Image.class);
    }

    @Step("Получение image по name {name}")
    public static Image getImageByName(String name) {
        List<Image> imageList = getImageList();
        for (Image image : imageList) {
            if (image.getName().equals(name)) {
                return image;
            }
        }
        return null;
    }

    @Step("Удаление image groups по id {id}")
    public static void deleteImageGroupById(String id) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .delete("/image_groups/{}", id)
                .assertStatus(200);
    }

    @Step("Удаление marketing по id {id}")
    public static void deleteMarketingById(String id) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .delete("/marketing/{}", id)
                .assertStatus(200);
    }

    @Step("Частичное обновление image groups по id {id}")
    public static void partialUpdateImageGroupById(String id, JSONObject body) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(body)
                .patch("/image_groups/{}", id)
                .assertStatus(200);
    }

    @Step("Частичное обновление marketing по id {id}")
    public static void partialUpdateMarketingById(String id, JSONObject body) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(body)
                .patch("/marketing/{}", id)
                .assertStatus(200);
    }
    @Step("Частичное обновление Image по id {id}")
    /*
      Метод patch для Image должен работать только на обновление информации о marketing
      в body метода передается json в формате : {"marketing_id" : "id"}
     */
    public static void partialUpdateImageById(String id, JSONObject body) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(body)
                .patch("/marketing/{}", id)
                .assertStatus(200);
    }

    @Step("Создание image groups")
    public static ImageGroups createImageGroup(JSONObject object) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post("/image_groups")
                .assertStatus(200)
                .extractAs(ImageGroups.class);
    }

    @Step("Создание marketing")
    public static Marketing createMarketing(JSONObject object) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post("/marketing")
                .assertStatus(200)
                .extractAs(Marketing.class);
    }

    @Step("Проверка существования image group c именем {name}")
    public static boolean isImageGroupExist(String name, boolean isNeedAll) {
        List<ImageGroups> imageGroupList = getImageGroupsList(isNeedAll);
        for (ImageGroups imageGroup : imageGroupList) {
            if (imageGroup.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка существования marketing c именем {name}")
    public static boolean isMarketingExist(String name) {
        List<Marketing> marketingList = getMarketingList();
        for (Marketing marketing : marketingList) {
            if (marketing.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
