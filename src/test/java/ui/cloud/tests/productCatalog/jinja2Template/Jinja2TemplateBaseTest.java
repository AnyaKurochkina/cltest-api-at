package ui.cloud.tests.productCatalog.jinja2Template;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

import java.util.UUID;

@Epic("Конструктор. Шаблоны Jinja2")
@DisabledIfEnv("prod")
public class Jinja2TemplateBaseTest extends ProductCatalogUITest {

    protected final String TITLE = "AT UI Jinja2 Template";
    protected final String NAME = UUID.randomUUID().toString().substring(29);
    protected final String DESCRIPTION = "Description";
    protected String template = "{%- set acl_fullname = [] -%}\n" +
            "{%- for policy in policies -%}\n" +
            "{%- do acl_fullname.append(project_name +\"-\"+ policy) -%}\n" +
            "{%- endfor -%}\n" +
            "{{ acl_fullname | tojson(indent=2) }}";
    protected String data = "{\"name\":\"cloud-crux-polrs4\",\"policies\":[\"portal-ro\",\"user-ro\"]," +
            "\"project_name\":\"kv-proj-k0x1pshcki\"}";
    protected Jinja2Template jinja2Template;
    protected Action action;
    protected Graph graph;

    @BeforeEach
    public void setUp() {
        createJinja2Template(NAME);
    }

    protected void createJinja2Template(String name) {

        jinja2Template = Jinja2Template.builder()
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .jinja2Template(template)
                .jinja2Data(new JSONObject(data))
                .build()
                .createObject();
    }
}
