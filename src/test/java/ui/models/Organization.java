package ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Organization {
    private String name;
    private String email;
    private String INN;
}
