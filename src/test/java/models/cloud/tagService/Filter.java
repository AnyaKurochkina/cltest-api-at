package models.cloud.tagService;

import core.helper.http.QueryBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import models.cloud.keyCloak.UserInfo;

import java.util.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Filter {
    List<String> inventoryPks;
    Tag tags;
    Tag excludingTags;
    List<String> responseTags;
    List<String> requiredTags;
    Boolean contextPathIsnull;
    Boolean allowEmptyTagFilter;
    List<String> dataSources;
    @Singular
    Map<String, InventoryAttrs> inventoryFilters;
    List<String> inventoryTypes;
    List<String> roles;
    UserInfo impersonate;

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

        public Tag addInnerFilter(String tagOperator, List<TagFilter> innerFilters){
            if(Objects.isNull(filters))
                filters = new ArrayList<>();
            filters.add(new TagFilter("inner_filter", tagOperator, innerFilters));
            return this;
        }

        public Tag setTagOperator(String tagOperator) {
            this.tagOperator = tagOperator;
            return this;
        }

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class TagFilter {
            String lookup;
            String key;
            Object value;
            String tagOperator;

            public TagFilter(String key, List<String> value) {
                this.key = key;
                this.value = value;
            }

            public TagFilter(String key, String value) {
                this.key = key;
                this.value = Collections.singletonList(value);
            }

            public TagFilter(String key, String value, String lookup) {
                this.lookup = lookup;
                this.key = key;
                this.value = value;
            }

            public TagFilter(String lookup, String tagOperator, List<TagFilter> filters) {
                this.lookup = lookup;
                this.tagOperator = tagOperator;
                this.value = filters;
            }
        }
    }

    @Data @AllArgsConstructor
    public static class InventoryAttrs {
        List<InventoryFilter> filters;

        @Data @Builder @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class InventoryFilter {
            String id;
            String key;
            String lookup;
            String value;
        }
    }


    @Getter
    public static class Query extends QueryBuilder {
        String ordering;

        private Query() {}

        public static Builder builder() {
            return new Query().new Builder();
        }

        public class Builder {
            StringJoiner str = new StringJoiner(",");
            public Builder addOrder(String order) {
                str.add(order);
                return this;
            }
            public Builder addOrderDesc(String order) {
                str.add("-" + order);
                return this;
            }
            public Query build() {
                ordering = str.toString();
                return Query.this;
            }
        }
    }
}
