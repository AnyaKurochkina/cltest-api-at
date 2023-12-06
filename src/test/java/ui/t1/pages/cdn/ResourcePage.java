package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import models.t1.cdn.Resource;
import ui.elements.Button;
import ui.elements.Dialog;

import static core.helper.StringUtils.*;

public class ResourcePage {

    Button editButton = Button.byText("Редактировать");

    public ResourcePage(String resourceName) {
        $x("//span[text() = '{}']", resourceName).shouldBe(Condition.visible);
    }

    public void editResource(Resource resource) {
        editButton.click();
        Dialog editDialog = Dialog.byTitle("Редактировать ресурс");
        int hostNameSize = resource.getHostnames().size();
        int size = $$x("//label[text() = 'Доменное имя']").size();
        int difference = hostNameSize - size;
        if (difference > 0) {
            for (int i = size; i < hostNameSize; i++) {
                editDialog.clickButton("Добавить");
                editDialog.setInputByName(format("hostnames-{}", i), resource.getHostnames().get(i));
            }
        }
    }
}
