package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.visualTeamplate.GetVisualTemplateList;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisualTemplateSteps extends Steps {

    private static final String visualTemplateUrl = "/api/v1/item_visual_templates/";

    @Step("Получение списка шаблонов отображения")
    public static List<ItemVisualTemplate> getVisualTemplateList() {
        //todo сравнение с jsonshema
        return  new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl)
                .assertStatus(200)
                .extractAs(GetVisualTemplateList.class).getList();
    }

    @Step("Получение шаблона отображения по Id")
    public static ItemVisualTemplate getVisualTemplateById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl + objectId + "/")
                .extractAs(ItemVisualTemplate.class);
    }

    @Step("Удаление шаблона отображения по Id")
    public static Response deleteVisualTemplateById(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(visualTemplateUrl + id + "/");
    }

    @Step("Получение списка шаблонов отображения отсортированного по дате создания")
    public static List<ItemVisualTemplate> getItemVisualTemplateListOrdering(String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl + "?ordering={}", filter)
                .assertStatus(200)
                .extractAs(GetVisualTemplateList.class)
                .getList();
    }

    @Step("Получение шаблона отображения по имени {name}")
    public static ItemVisualTemplate getVisualTemplateByName(String name) {
        List<ItemVisualTemplate> itemVisualTemplateList = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl + "?name={}", name)
                .extractAs(GetVisualTemplateList.class).getList();
        assertEquals(1, itemVisualTemplateList.size(), "Размер списка должен быть 1");
        return itemVisualTemplateList.get(0);
    }

    @Step("Удаление шаблона отображения по имени {name}")
    public static void deleteVisualTemplateByName(String name) {
        deleteVisualTemplateById(getVisualTemplateByName(name).getId())
                .assertStatus(204);
    }

    @Step("Получение шаблона визуализации по event_type и event_provider")
    public static ItemVisualTemplate getItemVisualTemplateByTypeProvider(String eventType, String eventProvider) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl + "item_visual_template/" + eventType + "/" + eventProvider + "/")
                .assertStatus(200)
                .extractAs(ItemVisualTemplate.class);
    }

    @Step("Проверка существования шаблона визуализации по имени")
    public static boolean isVisualTemplateExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Частичное обновление шаблона визуализации")
    public static Response partialUpdateVisualTemplate(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(visualTemplateUrl + id + "/")
                .assertStatus(200);
    }

    @Step("Получение Meta данных списка шаблонов визуализаций")
    public static Meta getMetaVisualTemplateList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl)
                .assertStatus(200)
                .extractAs(GetVisualTemplateList.class).getMeta();
    }

    @Step("Получение списка шаблонов визуализаций по фильтру")
    public static List<ItemVisualTemplate> getVisualTemplateListByFilter(String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(visualTemplateUrl + filter)
                .assertStatus(200)
                .extractAs(GetVisualTemplateList.class).getList();
    }
}
