package api.cloud.tagService;

import api.Tests;
import com.mifmif.common.regex.Generex;
import models.cloud.authorizer.Project;
import models.cloud.tagService.Context;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Tags;

import java.util.ArrayList;
import java.util.List;

@Tags({@org.junit.jupiter.api.Tag("tag_service"), @org.junit.jupiter.api.Tag("t1"), @org.junit.jupiter.api.Tag("regress")})
@DisabledIfEnv("prod")
public class AbstractTagServiceTest extends Tests {
    protected Project project = Project.builder().isForOrders(true).build().createObject();
    protected Context context = Context.byId(project.getId());

    protected String randomName(String prefix) {
        return new Generex("AT-" + prefix + "-[a-z]{6}").random();
    }

    protected String randomName() {
        return new Generex("AT-[a-z]{6}").random();
    }

    protected List<Tag> generateTags(int count) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < count; i++)
            tags.add(Tag.builder().key(randomName("tag" + i)).context(context).build().createObject());
        return tags;
    }

    protected List<Inventory> generateInventories(int count) {
        List<Inventory> inventories = new ArrayList<>();
        for (int i = 0; i < count; i++)
            inventories.add(Inventory.builder().context(context).build().createObjectPrivateAccess());
        return inventories;
    }
}
