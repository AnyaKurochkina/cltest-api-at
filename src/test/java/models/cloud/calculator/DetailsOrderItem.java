package models.cloud.calculator;

import lombok.Data;

import java.util.List;

@Data
public class DetailsOrderItem {
    private String accountId;
    private String folder;
    private List<DetailsItem> details;
    private List<Object> metrics;
}
