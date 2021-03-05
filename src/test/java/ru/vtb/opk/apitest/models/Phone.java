package ru.vtb.opk.apitest.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@DatabaseTable(tableName = "integration_zniis.mnp_phones")
@NoArgsConstructor
@ToString
public class Phone {
    @DatabaseField(columnName = "number")
    private String phone;
    @DatabaseField(columnName = "occ")
    private String operator;

    private String[] links = new String[0];

    public Map<String, String> toMap() {
        Map<String,String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("operator", operator);
        return map;
    }
}
