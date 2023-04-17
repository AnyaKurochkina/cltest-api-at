package ui.models;

import lombok.Data;

@Data
public class StorageProfile {
    private String name;
    private String limit;
    private boolean isMain;

    public StorageProfile(String name, String limit) {
        this.name = name;
        this.limit = limit;
    }
}
