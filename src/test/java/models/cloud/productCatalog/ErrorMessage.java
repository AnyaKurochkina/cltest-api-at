package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    @JsonProperty("err_message")
    List<String> errMessage;
    @JsonProperty("err_details")
    Object errDetails;

    public String getMessage() {
        return errMessage.get(0);
    }
}
