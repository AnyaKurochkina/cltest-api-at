package models.cloud.secretService;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import steps.secretService.SecretServiceAdminSteps;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SecretResponse extends AbstractEntity {
    private String updatedAt;
    private Engine engine;
    private String createdAt;
    private String id;
    private String uri;
    private List<String> tags;

    @Override
    public void delete() {
        SecretServiceAdminSteps.deleteV1SecretsId(id);
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Engine {
        private String updatedAt;
        private String segment;
        private String name;
        private String createdAt;
        private String id;
    }
}