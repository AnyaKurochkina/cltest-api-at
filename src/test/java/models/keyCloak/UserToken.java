package models.keyCloak;

import lombok.Builder;
import models.EntityOld;

@Builder
public class UserToken extends EntityOld {
    public String token;
    public long time;
}
