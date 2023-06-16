package models.cloud.tagService.v1;

import lombok.*;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateInventoryTags {
    @Singular("addCreatedDataSet")
    List<InventoryTagWithoutTagInfo> createdDataSet;
    @Singular("addUpdatedDataSet")
    List<InventoryTagWithoutTagInfo> updatedDataSet;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class InventoryTagWithoutTagInfo{
        String inventory;
        String tag;
        String value;
    }
}
