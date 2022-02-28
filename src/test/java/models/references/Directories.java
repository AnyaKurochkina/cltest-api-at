package models.references;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Directories {
    private String id;
    private String name;
    private String description;

    @Override
    public String toString() {
        return String.format("{\"id\": %s,\"name\": %s,\"description\": %s}", id, name, description);
    }
}
