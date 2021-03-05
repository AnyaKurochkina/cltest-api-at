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
@DatabaseTable(tableName = "integration_zniis.phone_ranges")
@NoArgsConstructor
@ToString
public class PhoneRanges {

    @DatabaseField(columnName = "phone_from")
    private String phone_from;

    @DatabaseField(columnName = "phone_to")
    private String phone_to;

    @DatabaseField(columnName = "occ")
    private String operator;

    @DatabaseField(columnName = "mnc")
    private String mnc;

    //Unused
//    @DatabaseField(columnName = "region_id")
//    private String regionId;

    private String[] links = new String[0];


}
