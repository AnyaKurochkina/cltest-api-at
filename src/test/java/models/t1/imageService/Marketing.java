package models.t1.imageService;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.t1.imageService.ImageServiceSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Marketing extends Entity {
    private String id;
    private String name;
    private String description;
    @JsonProperty("logo_id")
    private String logoId;
    private String logo;
    private List<Link> links;
    private Boolean support;
    private List<String> use_examples;
    private String tech_support;
    private List<String> type_paid;
    private String licence_agreement;
    private List<String> publisher;

    @Override
    public Entity init() {
        if (logoId == null) {
            Logo logo = Logo.builder().build().createObject();
            logoId = logo.getId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("t1/createMarketing.json")
                .set("$.name", name)
                .set("$.description", description)
                .set("$.logo", logo)
                .set("$.logo_id", logoId)
                .set("$.support", support)
                .set("$.links", links)
                .set("$.use_examples", use_examples)
                .set("$.tech_support", tech_support)
                .set("$.type_paid", type_paid)
                .set("$.publisher", publisher)
                .set("$.licence_agreement", licence_agreement)
                .build();
    }

    @Override
    protected void create() {
        if (isMarketingExist(name)) {
            Marketing marketingByName = getMarketingByName(name);
            deleteMarketingById(Objects.requireNonNull(marketingByName).getId());
        }
        Marketing marketing = createMarketing(toJson());
        StringUtils.copyAvailableFields(marketing, this);
        assertNotNull(id, "Marketing с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteMarketingById(id);
    }
}
