package models.cloud.tagService.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.helper.Date;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterResultV2 {
    String inventory;
    String contextPath;
    Map<String, String> tags;
    Date createdAt, updatedAt;
}
