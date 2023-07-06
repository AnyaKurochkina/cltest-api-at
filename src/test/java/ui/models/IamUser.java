package ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IamUser {
    private String email;
    private List<String> role;

    public void addRole(String role) {
        this.role.add(role);
    }
}
