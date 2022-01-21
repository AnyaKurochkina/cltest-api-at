package models.subModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DbUser {
    String nameDB;
    String username;
}