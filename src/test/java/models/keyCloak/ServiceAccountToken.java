package models.keyCloak;

import lombok.Builder;
import models.EntityOld;

@Builder
public class ServiceAccountToken extends EntityOld {
    public String token;
    public String serviceAccountName;
    public long time;
}
