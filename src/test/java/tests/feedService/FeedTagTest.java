package tests.feedService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.feedService.tag.FeedTag;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.feedService.FeedServiceSteps.*;

@Tag("feed_service")
@Tag("tags")
@Epic("Сервис новостей")
@Feature("Тэг")
@DisabledIfEnv("prod")
public class FeedTagTest extends Tests {

    @DisplayName("Создание тэга")
    @TmsLink("976790")
    @Test
    public void createTag() {
        FeedTag feedTag = FeedTag.builder()
                .title("create_tag_title_test_api")
                .key("create_tag_key_test_api")
                .parent(null)
                .build()
                .createObject();
        FeedTag createdFeedTag = getTagById(feedTag.getId());
        assertEquals(feedTag, createdFeedTag);
    }

    @DisplayName("Получение тэга по id")
    @TmsLink("976735")
    @Test
    public void getTagTest() {
        FeedTag feedTag = FeedTag.builder()
                .title("get_by_id_tag_title_test_api")
                .key("get_by_id_tag_key_test_api")
                .parent(null)
                .build()
                .createObject();
        FeedTag createdFeedTag = getTagById(feedTag.getId());
        assertEquals(feedTag, createdFeedTag);
    }

    @DisplayName("Получение списка тэгов")
    @TmsLink("976737")
    @Test
    public void getTagListTest() {
        FeedTag feedTag = FeedTag.builder()
                .title("get_list_tag_title_test_api")
                .key("get_list_tag_key_test_api")
                .parent(null)
                .build()
                .createObject();
        List<FeedTag> list = getTagList().getList();
        assertTrue(list.contains(feedTag));
    }

    @DisplayName("Обновление тэга")
    @TmsLink("976750")
    @Test
    public void updateTagTest() {
        FeedTag feedTag = FeedTag.builder()
                .title("update_tag_title_test_api")
                .key("update_tag_key_test_api")
                .parent(null)
                .build()
                .createObject();
        FeedTag expectedTag = FeedTag.builder()
                .title("updated_tag_title_test_api")
                .key("updated_tag_key_test_api")
                .build();
        JSONObject body = expectedTag.init().toJson();
        FeedTag actualTag = updateTag(feedTag.getId(), body);
        assertEquals(expectedTag.getTitle(), actualTag.getTitle());
        assertEquals(expectedTag.getKey(), actualTag.getKey());
    }

    @DisplayName("Частичное обновление тэга")
    @TmsLink("976767")
    @Test
    public void partialUpdateTagTest() {
        FeedTag feedTag = FeedTag.builder()
                .title("partial_update_tag_title_test_api")
                .key("partial_update_tag_key_test_api")
                .parent(null)
                .build()
                .createObject();
        String expectedTitle = "partial_update";
        JSONObject body = new JSONObject().put("title", expectedTitle);
        FeedTag actualFeedTag = partialUpdateTag(feedTag.getId(), body);
        assertEquals(expectedTitle, actualFeedTag.getTitle());
    }

    @DisplayName("Удаление тэга")
    @TmsLink("976775")
    @Test
    public void deleteTagTest() {
        FeedTag feedTag = FeedTag.builder()
                .title("delete_tag_title_test_api")
                .key("delete_tag_key_test_api")
                .parent(null)
                .build()
                .createObject();
        deleteTag(feedTag.getId());
        List<FeedTag> list = getTagList().getList();
        assertFalse(list.contains(feedTag));
    }
}
