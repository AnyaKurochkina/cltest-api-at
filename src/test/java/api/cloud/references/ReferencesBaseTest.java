package api.cloud.references;

import api.Tests;
import models.cloud.references.Directories;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Tag;

@DisabledIfEnv("prod")
@Tag("references")
public class ReferencesBaseTest extends Tests {
    public static final String DIRECTORIES_DESCRIPTION = "test_api";

    public JSONObject createDirectoryJson(String name, String description) {
        return Directories.builder()
                .name(name)
                .description(description)
                .build()
                .toJson();
    }
}
