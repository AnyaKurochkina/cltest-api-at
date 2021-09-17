package models.keyCloak;

import lombok.Builder;
import models.EntityOld;

@Builder
public class Service extends EntityOld {
    public String clientId;
    public String clientSecret;
}
