package models.subModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostgreSqlUsers {
    String nameDB;
    String username;
    @Builder.Default
    public boolean isDeleted = false;
}