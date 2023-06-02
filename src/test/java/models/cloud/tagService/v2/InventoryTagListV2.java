package models.cloud.tagService.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryTagListV2 {
    String id;
    Inventory inventory;
    Tag tag;
    String value;
    String author;
    AuthorInfo authorInfo;

    @Data
    public static class AuthorInfo {
        String email;
        String lastName;
        String firstName;
    }
}
