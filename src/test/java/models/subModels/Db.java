package models.subModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Db {
    String nameDB;
    public boolean isDeleted = false;
}
