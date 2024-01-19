package models.cloud.orderService.products;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class GenericArangoDB extends Astra {

    @Override
    public Entity init() {
        jsonTemplate = "/orders/generic_arango_db.json";
        productName = "Generic ArangoDB";
        super.init();
        return this;
    }
}
