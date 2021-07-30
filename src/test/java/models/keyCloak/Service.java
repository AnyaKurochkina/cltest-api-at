package models.keyCloak;

import lombok.Builder;
import models.Entity;

@Builder
public class Service extends Entity {
    public String clientId;
    public String clientSecret;
}
