package api.cloud.productCatalog.tag;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ImportObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ProductSteps.importProduct;
import static steps.productCatalog.TagSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("теги")
@DisabledIfEnv("prod")
public class TagImportTest extends Tests {

    @DisplayName("Импорт Тега")
    @TmsLink("1698521")
    @Test
    public void importTagTest() {
        String importTagName = createTagByName("import_tag_test_api").getName();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/tag/importTag.json";
        DataFileHelper.write(filePath, exportTagByName(importTagName).toString());
        deleteTagByName(importTagName);
        ImportObject importObject = importTag(filePath);
        DataFileHelper.delete(filePath);
        assertEquals(importTagName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isTagExists(importTagName), "Тега не существует");
        deleteTagByName(importTagName);
        assertFalse(isTagExists(importTagName), "Тег существует");
    }

    @DisplayName("Импорт нескольких Тегов")
    @TmsLink("1698534")
    @Test
    public void importTagsTest() {
        String importTagName = createTagByName("multi_import_tag_test_api").getName();
        String importTagName2 = createTagByName("multi_import_tag2_test_api").getName();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/tag/multiImportTag.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/tag/multiImportTag2.json";
        DataFileHelper.write(filePath, exportTagByName(importTagName).toString());
        DataFileHelper.write(filePath2, exportTagByName(importTagName2).toString());
        deleteTagByName(importTagName);
        deleteTagByName(importTagName2);
        importObjects("tags", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isTagExists(importTagName), "Тега не существует");
        assertTrue(isTagExists(importTagName2), "Тега не существует");
        deleteTagByName(importTagName);
        deleteTagByName(importTagName2);
    }

    @DisplayName("Импорт уже существующего Тега")
    @TmsLink("1698560")
    @Test
    public void importExistTagTest() {
        String tagName = createTagByName("import_exist_tag_test_api").getName();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/tag/existTagImport.json";
        DataFileHelper.write(filePath, exportTagByName(tagName).toString());
        ImportObject importObject = importTag(filePath);
        deleteTagByName(tagName);
        DataFileHelper.delete(filePath);
        assertEquals("success", importObject.getStatus());
        assertEquals( String.format("Обновлен объект Tag %s", tagName), importObject.getMessages().get(0));
    }

    @Test
    @DisplayName("Негативный тест импорт Тега в другой раздел")
    @TmsLink("")
    public void importTagToAnotherSection() {
        String importTagName = createTagByName("import_tag_to_another_section_test_api").getName();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/tag/importTagToAnotherSection.json";
        DataFileHelper.write(filePath, exportTagByName(importTagName).toString());
        importProduct(filePath);
        assertTrue(isTagExists(importTagName), "Тега не существует");
        deleteTagByName(importTagName);
        assertFalse(isTagExists(importTagName), "Тег существует");
    }
}
