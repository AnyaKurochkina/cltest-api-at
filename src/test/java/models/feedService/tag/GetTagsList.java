package models.feedService.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.feedService.Meta;

import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTagsList {
	private Meta meta;
	private List<FeedTag> list;
}