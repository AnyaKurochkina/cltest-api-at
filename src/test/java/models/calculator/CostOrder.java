package models.calculator;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CostOrder {
	private Double total;
	private Double cost;
	private Date removeDt;
	private Date createDt;
	private List<DetailsOrderItem> details;
	private String uuid;
}