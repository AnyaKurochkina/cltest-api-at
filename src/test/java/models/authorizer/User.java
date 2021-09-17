package models.authorizer;

import lombok.Builder;
import models.EntityOld;

@Builder
public class User extends EntityOld {
    public String username;
    public String password;
}
