package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import models.AbstractEntity;
import steps.t1.cdn.CdnOriginGroupsClient;

import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceGroup extends AbstractEntity {

    private String id;
    private String name;
    private List<Origin> originIds;
    @JsonProperty("useNext")
    private Object useNext;
    private Object origins;
    private Object path;
    private Object hasRelatedResources;
    private String projectId;
    private String domainName;
    @Builder.Default
    private Boolean isReserved = false;

    @Override
    public void delete() {
        CdnOriginGroupsClient.deleteSourceGroupByName(projectId, name);
    }
}
