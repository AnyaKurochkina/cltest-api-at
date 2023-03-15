package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Filter {
    Integer page;
    Integer perPage;
    List<String> inventoryPks;
    Tag tags;
    Tag excludingTags;
    List<String> responseTags;
    List<String> requiredTags;
    Boolean contextPathIsnull;
    Boolean allowEmptyTagFilter;
    List<String> dataSources;
    Map<String, InventoryAttrs> inventoryFilters;
    List<String> inventoryTypes;
    List<String> roles;

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Tag {
        String tagOperator;
        List<TagFilter> filters;

        public Tag(String tagOperator) {
            this.tagOperator = tagOperator;
        }

        public Tag addFilter(TagFilter filter){
            if(Objects.isNull(filters))
                filters = new ArrayList<>();
            filters.add(filter);
            return this;
        }

        @Data @AllArgsConstructor
        public static class TagFilter {
            String key;
            List<String> value;
        }
    }

    @Data @Builder @AllArgsConstructor
    public static class InventoryAttrs {
        List<InventoryFilter> filters;

        @Data @Builder @AllArgsConstructor
        public static class InventoryFilter {
            String id;
            String key;
            String lookup;
            String value;
        }
    }
}
