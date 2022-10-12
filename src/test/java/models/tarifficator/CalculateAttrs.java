package models.tarifficator;

import lombok.Data;

import java.util.List;

@Data
public class CalculateAttrs{
	Integer amount;
	private String provider;
	private String environmentType;
	private List<String> calculationEntityFieldNamePath;
	private List<String> osPath;
	private String osType;
	private List<String> environment;
	private List<String> environmentPath;
	private List<String> providerPath;
}