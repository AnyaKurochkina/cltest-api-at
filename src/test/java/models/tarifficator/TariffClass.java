package models.tarifficator;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TariffClass {
    String environmentType;
    String provider;
    List <String> providerPath;
    Map<String, String> calculationEntity;
    String calculationEntityFieldName;
    String createdAt;
    String id;
    List<String> itemStates;
    String kind;
    String name;
    float price;
    String priceUnit;
    String resourceType;
    String script;
    String tariffPlanId;
    String title;
    String updatedAt;
}
