package models.cloud.tarifficator;

import lombok.Data;

import java.util.List;

@Data
public class TariffPlanServices{
	private String tariffPlanId;
	private String updatedAt;
	private String name;
	private String createdAt;
	private String description;
	private String id;
	private String title;
	private List<TariffClass> tariffClasses;
}