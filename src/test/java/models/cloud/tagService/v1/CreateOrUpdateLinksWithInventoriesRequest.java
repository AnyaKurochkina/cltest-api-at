package models.cloud.tagService.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateOrUpdateLinksWithInventoriesRequest {
    String dataSource;
    @Singular
    List<InventoryValueRequest> inventories;

    @Data
    @AllArgsConstructor
    public static class InventoryValueRequest{
        String inventory;
        String value;
    }
}
