package models.cloud.keyCloak;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfo {
    String name;
    String sub;
    String preferredUsername;
    String email;
    String emailVerified;
    String familyName;
    String givenName;
    String middleName;
    String locale;
}
