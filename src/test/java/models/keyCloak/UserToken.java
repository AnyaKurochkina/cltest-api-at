package models.keyCloak;

import lombok.Builder;
import models.Entity;

@Builder
public class UserToken extends Entity {
    public String token;
    public long time;
}
