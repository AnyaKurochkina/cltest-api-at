package ui.models.cloudDirector;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vdc {
    private String name;
    private String cpu;
    private String ram;
    private StorageProfile storageProfile;
}
