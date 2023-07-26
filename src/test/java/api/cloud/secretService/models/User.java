package api.cloud.secretService.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    private Date updatedAt;
    private String keycloakId;
    private String name;
    private Date createdAt;
    private String id;
    private String email;
    private String username;
}