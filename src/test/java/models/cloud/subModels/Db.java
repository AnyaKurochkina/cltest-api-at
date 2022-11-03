package models.cloud.subModels;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Db {
    String nameDB;

    public Db(String nameDB) {
        this.nameDB = nameDB;
    }
}
