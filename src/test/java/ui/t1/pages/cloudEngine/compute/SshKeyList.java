package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

public class SshKeyList {
    public static final String SSH_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCSI82vsEXJoV4Co1HPjUd8ldwjTRbJsE27yzpc3rcxfqIrB9vte7J0YkCCXuZZsYEufIMYWXcXHOLJEqLnoJsp0EjJ5wOVBc6I10WozLm458P0mwPEbc6N5Z0MQ8gZk3i3yOap+G9owWMirlfArz2afKL4E+6rXfY+XpfPceGPJ8dGDWvuMnvwIYWenz8HwBRvQwR8FtJyUOP7sOdsuTz6T+E+qQiuvBY0ciUwAaFbGWhKtgk7dJd73ZxZIZFg3jFxySScePcEsf4nC+61siqqaSBzLk+jyNbrURTeQ0ZYoYR3jMexgUAY/8cNki89U/OfWNBG6jqCWn/K2BcgX1cl";


    /*
new SshKeysPage().addKey("superKey", "root")
new SshKeysPage().editKey("superKey", "superKey2");
new SshKeysPage().copyKey("superKey2");
new SshKeysPage().deleteKey("superKey2");
     */

    public void addKey(String nameKey, String login) {
        if(new KeysTable().isColumnValueEquals(KeysTable.COLUMN_NAME, nameKey))
            return;
        new KeysTable().clickAdd();
        Dialog.byTitle("Добавление SSH-ключа")
                .setInputValue("Название ключа", nameKey)
                .setInputValue("Логин пользователя", login)
                .setTextarea(TextArea.byName("sshKeyData"), SSH_KEY)
                .clickButton("Добавить");
        Alert.green("SSH-ключ {} создан успешно", nameKey);
        Assertions.assertTrue(new KeysTable().isColumnValueEquals(KeysTable.COLUMN_NAME, nameKey));
    }

    public void copyKey(String nameKey) {
        Menu.byElement(new KeysTable().getRowByColumnValue(KeysTable.COLUMN_NAME, nameKey).getElementByColumn("")).select("Скопировать");
        Alert.green("SSH-ключ {} скопирован", nameKey);
        Assertions.assertEquals(SSH_KEY, Selenide.clipboard().getText());
    }

    public void deleteKey(String nameKey) {
        Menu.byElement(new KeysTable().getRowByColumnValue(KeysTable.COLUMN_NAME, nameKey).getElementByColumn("")).select("Удалить");
        Dialog.byTitle("Подтверждение удаления").clickButton("Удалить");
        Alert.green("SSH-ключ {} удален успешно", nameKey);
        Assertions.assertFalse(new KeysTable().isColumnValueEquals(KeysTable.COLUMN_NAME, nameKey));
    }

    public void editKey(String nameKey, String newNameKey) {
        Menu.byElement(new KeysTable().getRowByColumnValue(KeysTable.COLUMN_NAME, nameKey).getElementByColumn("")).select("Редактировать");
        Dialog.byTitle("Редактирование SSH-ключа")
                .setInputValue("Название ключа", newNameKey)
                .clickButton("Сохранить");
        Assertions.assertTrue(new KeysTable().isColumnValueEquals(KeysTable.COLUMN_NAME, newNameKey));
    }

    private static class KeysTable extends DataTable {
        public static final String COLUMN_NAME = "Название";

        public KeysTable() {
            super(COLUMN_NAME);
        }
    }
}
