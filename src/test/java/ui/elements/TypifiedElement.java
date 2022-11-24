package ui.elements;

import com.codeborne.selenide.Selenide;

public interface TypifiedElement {
    String scrollCenter = "{block: 'center'}";
    String postfix = "[{}]";

    static void refresh(){
        Selenide.refresh();
        checkProject();
    }

    static String getIndex(int index){
        return (index == -1)?"last()":String.valueOf(index);
    }

    //TODO: До фикса доступа к балансу учеток закрываем все окна
    static void checkProject(){
//        new Alert().checkColor(Alert.Color.GREEN).checkText("Выбран контекст").close();
        new Alert().closeAll();
    }

    static void open(String url){
        Selenide.open(url);
        checkProject();
    }
}
