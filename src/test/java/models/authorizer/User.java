package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class User extends Entity {
    public String username;
    public String password;

    @Override
    public void create() {
    }

    @Override
    public void delete() {

    }
}
