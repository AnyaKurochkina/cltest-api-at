package models.tarifficator;

import lombok.Data;

import java.util.List;

@Data
public class CalculateAttrs{
	private String provider;
	private List<String> providerPath;
	private List<String> environmentTypePath;
	private String environmentType;
	private List<String> calculationEntityFieldNamePath;
	private List<String> osPath;
	private String osType;
}