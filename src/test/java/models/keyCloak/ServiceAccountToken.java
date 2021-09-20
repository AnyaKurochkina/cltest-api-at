package models.keyCloak;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class ServiceAccountToken extends Entity {
    public String token;
    public String serviceAccountName;
    public long time;

    @Override
    public Entity create() {
        return null;
    }

    @Override
    public void delete() {

    }
}
