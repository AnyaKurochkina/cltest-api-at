package models.cloud.authorizer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class UserList {
    private List<UserItem> data;
    private Meta meta;

    @Setter
    public static class Meta{
        private Integer totalCount;
    }
}

