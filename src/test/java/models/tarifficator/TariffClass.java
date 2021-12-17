package models.tarifficator;

import lombok.Data;

import java.util.List;

@Data
public class TariffClass {
	private Double amount;
	private String tariffPlanId;
	private CalculationEntity calculationEntity;
	private String kind;
	private String resourceType;
	private String tariffPlanServiceId;
	private String title;
	private String script;
	private String priceUnit;
	private Double price;
	private String calculationEntityFieldName;
	private String name;
	private String id;
	private List<String> itemStates;
	private CalculateAttrs calculateAttrs;
}