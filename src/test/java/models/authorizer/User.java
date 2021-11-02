package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class User extends Entity {
    public String username;
    public String password;

    @Override
    protected void create() {
    }

    @Override
    protected void delete() {

    }
}
