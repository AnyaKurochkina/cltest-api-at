package models.authorizer;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserItem {
    private String firstname;
    private Date updatedAt;
    private List<MembersItem> members;
    private Boolean active;
    private Date createdAt;
    private String id;
    private String email;
    private String username;
    private String lastname;
}
