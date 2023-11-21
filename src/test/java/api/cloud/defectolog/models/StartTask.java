package api.cloud.defectolog.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StartTask {
    @Builder.Default
    private String taskName = "start_validation_task";
    @Builder.Default
    private List<Object>argsParam = new ArrayList<>();
    private KwargsParam kwargsParam;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KwargsParam {
        @Builder.Default
        private String mode = "incremental";
        @Builder.Default
        private Integer updatedAtDelta = 1;
        @Singular
        private List<String> taskValidators;
    }
}
