package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import models.AbstractEntity;

import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class SourceGroup extends AbstractEntity {
    private String id;
    private String name;
    @JsonProperty("origin_ids")
    private List<Origin> originsList;
    private Object useNext;
    private Object origins;
    private Object path;
    private Object has_related_resources;

    @Override
    public void delete() {

    }
}
