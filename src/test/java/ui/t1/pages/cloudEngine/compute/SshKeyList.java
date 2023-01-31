package ui.t1.pages.cloudEngine.compute;

import core.helper.StringUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.util.List;

public class SshKeyList {
    public static final String SSH_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCSI82vsEXJoV4Co1HPjUd8ldwjTRbJsE27yzpc3rcxfqIrB9vte7J0YkCCXuZZsYEufIMYWXcXHOLJEqLnoJsp0EjJ5wOVBc6I10WozLm458P0mwPEbc6N5Z0MQ8gZk3i3yOap+G9owWMirlfArz2afKL4E+6rXfY+XpfPceGPJ8dGDWvuMnvwIYWenz8HwBRvQwR8FtJyUOP7sOdsuTz6T+E+qQiuvBY0ciUwAaFbGWhKtgk7dJd73ZxZIZFg3jFxySScePcEsf4nC+61siqqaSBzLk+jyNbrURTeQ0ZYoYR3jMexgUAY/8cNki89U/OfWNBG6jqCWn/K2BcgX1cl";

    @Step("Добавить ключ {nameKey}")
    public void addKey(String nameKey, String login) {
        if (new KeysTable().isColumnValueEquals(Column.TITLE, nameKey))
            return;
        new KeysTable().clickAdd();
        Dialog.byTitle("Добавление SSH-ключа")
                .setInputValue("Название ключа", nameKey)
                .setInputValue("Логин пользователя", login)
                .setTextarea(TextArea.byName("sshKeyData"), SSH_KEY)
                .clickButton("Добавить");
        Alert.green("SSH-ключ {} создан успешно", nameKey);
        Assertions.assertTrue(new KeysTable().isColumnValueEquals(Column.TITLE, nameKey));
    }

    @Step("Скопировать ключ {nameKey}")
    public void copyKey(String nameKey) {
        KeysTable.getMenuKey(nameKey).select("Скопировать");
        Alert.green("SSH-ключ {} скопирован", nameKey);
        Assertions.assertEquals(SSH_KEY, StringUtils.getClipBoardText());
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
        Dialog.byTitle("Редактирование SSH-ключа")
                .setInputValue("Название ключа", newNameKey)
                .clickButton("Сохранить");
        Assertions.assertTrue(new KeysTable().isColumnValueEquals(Column.TITLE, newNameKey));
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
