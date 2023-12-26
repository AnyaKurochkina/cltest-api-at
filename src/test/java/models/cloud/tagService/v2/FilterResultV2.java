package models.cloud.tagService.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.helper.Json;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterResultV2 {
    private String inventory;
    private String contextPath;
    private Map<String, Json> tags;
    private LocalDateTime createdAt, updatedAt;
}
