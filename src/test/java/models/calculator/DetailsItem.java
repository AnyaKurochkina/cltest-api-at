package models.calculator;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class DetailsItem implements Comparable<DetailsItem>{
	private Double total;
	private Double cost;
	private Date dtFrom;
	private List<Map<String, Object>> resources;
	private Date dtTo;

	@Override
	public int compareTo(@NotNull DetailsItem o) {
		return dtFrom.compareTo(o.getDtFrom());
	}
}