package models.keyCloak;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class Service extends Entity {
    public String clientId;
    public String clientSecret;

    @Override
    public void create() {
    }

    @Override
    public void delete() {

    }
}
