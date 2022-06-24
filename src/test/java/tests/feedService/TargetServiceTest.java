package tests.feedService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.feedService.targetService.TargetService;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static steps.feedService.FeedServiceSteps.*;

@Tag("feed_service")
@Epic("Сервис новостей")
@Feature("Потребитель событий")
@DisabledIfEnv("prod")
public class TargetServiceTest extends Tests {

    @DisplayName("Создание потребителя событий")
    @TmsLink("953716")
    @Test
    public void createTargetService() {
        TargetService targetService = TargetService.builder()
                .title("create_target_service_title_test_api")
                .internalName("create_target_service_internalName_test_api")
                .build()
                .createObject();
        TargetService createdTargetService = getTargetServiceById(targetService.getId());
        assertEquals(targetService, createdTargetService);
    }

    @DisplayName("Получение потребителя событий по id")
    @TmsLink("953901")
    @Test
    public void getTargetServiceTest() {
        TargetService targetService = TargetService.builder()
                .title("get_target_service_title_test_api")
                .internalName("get_target_service_internalName_test_api")
                .build()
                .createObject();
        TargetService createdTargetService = getTargetServiceById(targetService.getId());
        assertEquals(targetService, createdTargetService);
    }

    @DisplayName("Получение списка потребителя событий")
    @TmsLink("953805")
    @Test
    public void getTargetServiceListTest() {
        TargetService targetService = TargetService.builder()
                .title("get_list_target_service_title_test_api")
                .internalName("get_list_target_service_type_internalName_test_api")
                .build()
                .createObject();
        List<TargetService> list = getTargetServiceList().getList();
        assertTrue(list.contains(targetService));
    }

    @DisplayName("Обновление потребителя событий")
    @TmsLink("953819")
    @Test
    public void updateTargetServiceTest() {
        TargetService targetService = TargetService.builder()
                .title("update_target_service_title_test_api")
                .internalName("update_target_service_internalName_test_api")
                .build()
                .createObject();
        String title = "updated_target_service_title_test_api";
        TargetService expectedTargetService = TargetService.builder()
                .title(title)
                .internalName("updated_target_service_internalName_test_api")
                .build();
        JSONObject body = expectedTargetService.init().toJson();
        if (isTargetServiceExist(title)) {
            Integer id = Objects.requireNonNull(getTargetServiceByName(title)).getId();
            deleteTargetService(id);
        }
        TargetService actualTargetService = updateTargetService(targetService.getId(), body);
        assertEquals(expectedTargetService.getTitle(), actualTargetService.getTitle());
        assertEquals(expectedTargetService.getInternalName(), actualTargetService.getInternalName());
    }

    @DisplayName("Частичное обновление потребителя событий")
    @TmsLink("953898")
    @Test
    public void partialUpdateTargetServiceTest() {
        TargetService targetService = TargetService.builder()
                .title("partial_update_target_service_title_test_api")
                .internalName("partialUpdate_target_service_internalName_test_api")
                .build()
                .createObject();
        String expectedInternalName = "partial_update";
        JSONObject body = new JSONObject().put("internalName", expectedInternalName);
        TargetService actualTargetService = partialUpdateTargetService(targetService.getId(), body);
        assertEquals(expectedInternalName, actualTargetService.getInternalName());
    }

    @DisplayName("Удаление потребителя событий")
    @TmsLink("953902")
    @Test
    public void deleteTargetServiceTest() {
        TargetService targetService = TargetService.builder()
                .title("delete_target_service_title_test_api")
                .internalName("delete_target_service_internalName_test_api")
                .build()
                .createObject();
        deleteTargetService(targetService.getId());
        List<TargetService> list = getTargetServiceList().getList();
        assertFalse(list.contains(targetService));
    }
}
