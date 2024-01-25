package api.cloud.defectolog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Entity;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Isolated;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Isolated
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Дефектолог")
@Feature("Проверка создания дефектов INV-MM-ATTRS")
public class DefectologMMTest extends AbstractDefectologTest {

    private static final String SYS_ITEM_MAINTENANCE_MODE = "sys_item_maintenance_mode";
    private static final String SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA = "sys_item_maintenance_mode_extra_data";
    private static final String INV_MM_ATTRS = "INV-MM-ATTRS";

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private static class MaintenanceModeExtraData {
        private String author;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[ZZZZZ]")
        private ZonedDateTime updAt;
        private String code;
        private String info;
        private String ttl;
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode. not_exists_attrs")
    void notExistsAttrs() {
        Inventory inventory = generateInventories(1, context).get(0);
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[notExistsAttrs:[sys_item_maintenance_mode]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode. invalid_value. String")
    void sysItemMaintenanceModeStringValue() {
        Inventory inventory = generateInventories(1, context).get(0);
        inventoryTagsV2(context, inventory.getId(), null,
                Collections.singletonList(new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, randomName())));
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[invalidValue:[sys_item_maintenance_mode]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode. Отсутствие дефекта")
    void sysItemMaintenanceModeFalseValue() {
        Inventory inventory = generateInventories(1, context).get(0);
        inventoryTagsV2(context, inventory.getId(), null,
                Collections.singletonList(new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "false")));
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertNull(readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. invalid_value. String")
    void sysItemMaintenanceModeInvalidValueString() {
        Inventory inventory = generateInventories(1, context).get(0);
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA, randomName())));
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[invalidValue:[sys_item_maintenance_mode_extra_data]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. Отсутствует upd_at")
    void sysItemMaintenanceModeInvalidUpdAt() {
        Inventory inventory = generateInventories(1, context).get(0);
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA,
                        new JSONObject().put("author", randomName()).put("code", randomName()).put("info", randomName()).put("ttl", "72").toString())));
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[invalidValue:[sys_item_maintenance_mode_extra_data]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. Возраст меньше на минуту ttl")
    void sysItemMaintenanceModeLessTtl() {
        Inventory inventory = generateInventories(1, context).get(0);
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        MaintenanceModeExtraData extraData = MaintenanceModeExtraData.builder().author(randomName()).ttl("1")
                .code(randomName()).info(randomName()).updAt(dateFrom.minusMinutes(59)).build();
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA, Entity.serialize(extraData).toString())));
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertNull(readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. Возраст больше на минуту ttl")
    void sysItemMaintenanceModeMoreTtl() {
        Inventory inventory = generateInventories(1, context).get(0);
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        MaintenanceModeExtraData extraData = MaintenanceModeExtraData.builder().author(randomName()).ttl("1")
                .code(randomName()).info(randomName()).updAt(dateFrom.minusHours(1)).build();
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA, Entity.serialize(extraData).toString())));
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[invalidValue:[sys_item_maintenance_mode]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. Некорректный ttl")
    void sysItemMaintenanceModeIncorrectTtl() {
        Inventory inventory = generateInventories(1, context).get(0);
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        MaintenanceModeExtraData extraData = MaintenanceModeExtraData.builder().author(randomName()).ttl(randomName())
                .code(randomName()).info(randomName()).updAt(dateFrom).build();
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA, Entity.serialize(extraData).toString())));
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[invalidValue:[sys_item_maintenance_mode_extra_data]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. Отсутствует author")
    void sysItemMaintenanceModeEmptyAuthor() {
        Inventory inventory = generateInventories(1, context).get(0);
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        MaintenanceModeExtraData extraData = MaintenanceModeExtraData.builder().ttl("1").code(randomName())
                .info(randomName()).updAt(dateFrom).build();
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA, Entity.serialize(extraData).toString())));
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[notExistsKeys:[author]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }

    @Test
    @DisplayName("Тег sys_item_maintenance_mode_extra_data. Некорректный upd_at")
    void sysItemMaintenanceModeIncorrectUpdAt() {
        Inventory inventory = generateInventories(1, context).get(0);
        ZonedDateTime dateFrom = getDateFromFilter(inventory, context);
        MaintenanceModeExtraData extraData = MaintenanceModeExtraData.builder().ttl("1").code(randomName()).info(randomName()).author(randomName()).build();
        inventoryTagsV2(context, inventory.getId(), null, Arrays.asList(
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE, "true"),
                new InventoryTagsV2.Tag(SYS_ITEM_MAINTENANCE_MODE_EXTRA_DATA,
                        Entity.serialize(extraData).put("upd_at", "0").toString())));
        startTaskWidthGroups(INV_MM_ATTRS);

        int defectId = findDefectIdByInternalName(INV_MM_ATTRS, dateFrom);
        Assertions.assertEquals("[invalidValue:[sys_item_maintenance_mode_extra_data]]",
                readDefectPage(defectId).getExtInfo().jsonPath().getString(inventory.getId()));
    }
}
