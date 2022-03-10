package models.authorizer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class UserList {
    private List<User> data;
    private Meta meta;

    @Setter
    public static class Meta{
        private Integer totalCount;
    }
}

