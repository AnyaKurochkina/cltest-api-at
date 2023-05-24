package core.helper.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import models.Entity;

import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QueryBuilder{
    private final transient StringJoiner query = new StringJoiner("&", "?", "");

    public QueryBuilder add(Object key, Object value){
        query.add(key + "=" + value);
        return this;
    }

    @Override
    public String toString() {
        Entity.serialize(this).toMap().forEach(this::add);
        return query.toString().length() == 1 ? "" : query.toString();
    }
}
