package api.cloud.secretService.models;

import api.cloud.secretService.steps.SecretServiceAdminSteps;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import models.AbstractEntity;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@EqualsAndHashCode(callSuper = false)
public class SecretResponse extends AbstractEntity {
    private Date updatedAt;
    private Engine engine;
    private Date createdAt;
    private String id;
    private String uri;
    private List<String> tags;

    private Boolean readOnlyAccess;
    private Boolean writeAccess;
    private User author;
    private Boolean available;

    @Override
    public void delete() {
        SecretServiceAdminSteps.deleteV1SecretsId(id);
    }
}