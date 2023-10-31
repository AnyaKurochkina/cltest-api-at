package models.cloud.tagService.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.helper.Date;
import lombok.Data;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryTagListV2 {
    String id;
    Inventory inventory;
    Tag tag;
    String value;
    String author;
    Date createdAt;
    Date updatedAt;
    Integer dataSource;
    AuthorInfo authorInfo;

    @Data
    public static class AuthorInfo {
        String email;
        String lastName;
        String firstName;
    }
}
