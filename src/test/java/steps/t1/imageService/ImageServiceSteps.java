package steps.t1.imageService;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.t1.imageService.Image;
import models.t1.imageService.ImageGroups;
import models.t1.imageService.Logo;
import models.t1.imageService.Marketing;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;
import java.util.Objects;

import static core.enums.Role.CLOUD_ADMIN;
import static core.helper.Configure.ImageService;
import static core.helper.Configure.ProductCatalogURL;

public class ImageServiceSteps extends Steps {

    private static final String apiUrl = "/api/v1";

    @Step("Полуение списка image groups")
    public static List<ImageGroups> getImageGroupsList(boolean isNeedAll) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/image_groups?need_all={}", isNeedAll)
                .assertStatus(200)
                .jsonPath()
                .getList("", ImageGroups.class);
    }

    @Step("Полуение версии сервиса")
    public static Response getImageServiceVersion() {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/version")
                .assertStatus(200);
    }

    @Step("Получение статуса health")
    public static String getHealthStatusImageService() {
        return new Http(ProductCatalogURL)
                .setRole(CLOUD_ADMIN)
                .get("/api/v1/health/")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Полуение списка marketing")
    public static List<Marketing> getMarketingList() {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/marketing")
                .assertStatus(200)
                .jsonPath()
                .getList("", Marketing.class);
    }

    @Step("Полуение списка Logo")
    public static List<Logo> getLogoList() {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/logo")
                .assertStatus(200)
                .jsonPath()
                .getList("", Logo.class);
    }

    @Step("Полуение списка image")
    public static List<Image> getImageList() {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/images")
                .assertStatus(200)
                .jsonPath()
                .getList("", Image.class);
    }

    @Step("Полуение списка image groups по region {region}")
    public static List<ImageGroups> getImageGroupsListByRegion(String region) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/image_groups?availability_zone={}", region)
                .assertStatus(200)
                .jsonPath()
                .getList("", ImageGroups.class);
    }

    @Step("Получение image groups по id {id}")
    public static ImageGroups getImageGroup(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/image_groups/{}", id)
                .assertStatus(200)
                .extractAs(ImageGroups.class);
    }

    @Step("Получение logo по id {id}")
    public static Logo getLogoById(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/logo/{}", id)
                .assertStatus(200)
                .extractAs(Logo.class);
    }

    @Step("Получение image groups по name {name}")
    public static ImageGroups getImageGroupByName(String name) {
        List<ImageGroups> imageGroupList = getImageGroupsList(true);
        for (ImageGroups imageGroup : imageGroupList) {
            if (imageGroup.getName().equals(name)) {
                return imageGroup;
            }
        }
        return null;
    }

    @Step("Получение marketing по id {id}")
    public static Marketing getMarketingById(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .get(apiUrl + "/marketing/{}", id)
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
                .get(apiUrl + "/images/{}", id)
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
                .delete(apiUrl + "/image_groups/{}", id)
                .assertStatus(200);
    }

    @Step("Удаление image groups по name {name}")
    public static void deleteImageGroupByName(String name) {
        String id = Objects.requireNonNull(getImageGroupByName(name)).getId();
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .delete(apiUrl + "/image_groups/{}", id)
                .assertStatus(200);
    }

    @Step("Удаление marketing по id {id}")
    public static void deleteMarketingById(String id) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .delete(apiUrl + "/marketing/{}", id)
                .assertStatus(200);
    }

    @Step("Удаление logo по id {id}")
    public static Response deleteLogoById(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .delete(apiUrl + "/logo/{}", id)
                .assertStatus(200);
    }

    @Step("Удаление logo по id {id}")
    public static Response getDeleteLogoByIdResponse(String id) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .delete(apiUrl + "/logo/{}", id);
    }

    @Step("Частичное обновление image groups по id {id}")
    public static void partialUpdateImageGroupById(String id, JSONObject body) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(body)
                .patch(apiUrl + "/image_groups/{}", id)
                .assertStatus(200);
    }

    @Step("Частичное обновление logo по id {id}")
    public static void partialUpdateLogoById(String id, JSONObject body) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(body)
                .patch(apiUrl + "/logo/{}", id)
                .assertStatus(200);
    }

    @Step("Частичное обновление marketing по id {id}")
    /*
    При обновлении маркетинга, поле name обязательно в теле запроса.
     */
    public static void partialUpdateMarketingById(String id, JSONObject body) {
        new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(body)
                .patch(apiUrl + "/marketing/{}", id)
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
                .patch(apiUrl + "/marketing/{}", id)
                .assertStatus(200);
    }

    @Step("Создание image groups")
    public static ImageGroups createImageGroup(JSONObject object) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post(apiUrl + "/image_groups")
                .assertStatus(200)
                .extractAs(ImageGroups.class);
    }

    @Step("Создание marketing")
    public static Marketing createMarketing(JSONObject object) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post(apiUrl+ "/marketing")
                .assertStatus(200)
                .extractAs(Marketing.class);
    }

    @Step("Создание Logo")
    public static Logo createLogo(JSONObject object) {
        return new Http(ImageService)
                .setRole(CLOUD_ADMIN)
                .body(object)
                .post(apiUrl + "/logo")
                .assertStatus(200)
                .extractAs(Logo.class);
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

    @Step("Проверка существования logo по id {id}")
    public static boolean isLogoExist(String id) {
        List<Logo> logoList = getLogoList();
        for (Logo logo : logoList) {
            if (logo.getId().equals(id)) {
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
