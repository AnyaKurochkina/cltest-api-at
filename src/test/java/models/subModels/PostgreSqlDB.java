package models.subModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostgreSqlDB {
    String nameDB;
    @Builder.Default
    public boolean isDeleted = false;
}
