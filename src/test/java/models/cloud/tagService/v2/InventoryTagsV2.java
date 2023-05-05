package models.cloud.tagService.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InventoryTagsV2 {
    String dataSource;
    List<Tag> tags = new ArrayList<>();

    public InventoryTagsV2(String dataSource, List<Tag> tagList) {
        this.dataSource = dataSource;
        tagList.forEach(e -> tags.add(new Tag(e.getTag(), e.getValue())));
    }

    @Data
    @AllArgsConstructor
    public static class Tag {
        String tag;
        String value;
    }
}
