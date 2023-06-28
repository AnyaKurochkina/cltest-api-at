package models.cloud.tagService.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutInventoryRequest {
    @Singular
    List<PutInventory> inventories;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class PutInventory{
        String id;
        String contextPath;
        Boolean skipDefects;
        @Singular
        public List<Tag> tags;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @EqualsAndHashCode(exclude = {"id"})
        public static class Tag {
            public int id;
            public String tagKey;
            public String value;
            public String dataSource;

            public Tag() {}

            public Tag(String tagKey, String value, String dataSource) {
                this.tagKey = tagKey;
                this.value = value;
                this.dataSource = dataSource;
            }
        }
    }
}
