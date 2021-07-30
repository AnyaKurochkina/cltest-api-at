package models.keyCloak;

import lombok.Builder;
import models.Entity;

@Builder
public class ServiceAccountToken extends Entity {
    public String token;
    public String serviceAccountName;
    public long time;
}
