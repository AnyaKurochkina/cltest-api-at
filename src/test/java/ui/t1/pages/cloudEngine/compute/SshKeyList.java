package ui.t1.pages.cloudEngine.compute;

import core.helper.DataFileHelper;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.util.List;

public class SshKeyList {
    public static final String PUBLIC_KEY = DataFileHelper.read("src/test/resources/testData/id_rsa.pub");
    public static final String PRIVATE_KEY = "src/test/resources/testData/id_rsa";
    public static final String SSH_USER = "root";

    private final Input inputName = Input.byPlaceholder("введите название");
    private final Input inputLogin = Input.byPlaceholder("введите логин");
    private final Input inputKey = Input.byPlaceholder("введите или вставьте свой ключ");

    @Step("Добавить ключ {nameKey}")
    public void addKey(String nameKey, String login) {
        if (new KeysTable().isColumnValueEquals(Column.TITLE, nameKey))
            return;
        new KeysTable().clickAdd();
        Dialog.byTitle("Добавить SSH-ключ");
        inputName.setValue(nameKey);
        inputLogin.setValue(login);
        inputKey.setValue(PUBLIC_KEY);
        Button.byText("Добавить", -1).click();
        Alert.green("SSH-ключ {} создан успешно", nameKey);
        Assertions.assertTrue(new KeysTable().isColumnValueEquals(Column.TITLE, nameKey));
    }

    @Step("Скопировать ключ {nameKey}")
    public void copyKey(String nameKey) {
        KeysTable.getMenuKey(nameKey).select("Скопировать");
        Alert.green("SSH-ключ {} скопирован", nameKey);
        Assertions.assertEquals(PUBLIC_KEY, StringUtils.getClipBoardText());
    }

    @Step("Удалить ключ {nameKey}")
    public void deleteKey(String nameKey) {
        KeysTable.getMenuKey(nameKey).select("Удалить");
        Dialog.byTitle("Подтверждение удаления").clickButton("Удалить");
        Alert.green("SSH-ключ {} удален успешно", nameKey);
        Assertions.assertFalse(new KeysTable().isColumnValueEquals(Column.TITLE, nameKey));
    }

    @Step("Редактировать ключ {nameKey}")
    public void editKey(String nameKey, String newNameKey) {
        KeysTable.getMenuKey(nameKey).select("Редактировать");
        Dialog.byTitle("Изменить SSH-ключ");
        inputName.setValue(newNameKey);
        Button.byText("Изменить").click();
        Assertions.assertTrue(new KeysTable().isColumnValueEquals(Column.TITLE, newNameKey),
                StringUtils.format("Имя '{}' не было изменено на '{}'", nameKey, newNameKey));
    }

    public List<String> getSshKeysList() {
        return new KeysTable().getColumnValuesList(Column.TITLE);
    }

    private static class KeysTable extends DataTable {

        public KeysTable() {
            super(Column.TITLE);
        }

        public static Menu getMenuKey(String nameKey) {
            return Menu.byElement(new KeysTable().getRowByColumnValue(Column.TITLE, nameKey).getElementByColumnIndex(5));
        }
    }
}
