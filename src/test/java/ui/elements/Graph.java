package ui.elements;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.actions;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static core.helper.StringUtils.$x;

public class Graph implements TypifiedElement {

    SelenideElement canvas = $x("//canvas");
    SelenideElement btnZoomUp = $x("(//canvas/../..//descendant::button)[1]");
    SelenideElement btnZoomDown = $x("(//canvas/../..//descendant::button)[2]");

    private static final List<Color> colors = Arrays.asList(
            //Завершено
            new Color(76, 175, 80),
            //Запущено
            new Color(255, 179, 0),
            //Ошибка
            new Color(218, 11, 32),
            //Пропущено и завершено
            new Color(13, 105, 242),
            //Не начато
            new Color(158, 158, 158)
    );

    private List<Point> getCurrentNodeCoordinates() {
        List<Point> points = new ArrayList<>();
        for (Color c : colors)
            points.addAll(findNodesColor(c));
        return points;
    }

    private void checkCurrentNodes() {
        Rectangle rect = Selenide.$x("//canvas").getRect();
        for (Point point : getCurrentNodeCoordinates()) {
            actions().moveToElement(Selenide.$x("//canvas").getWrappedElement(),
                    point.getX() - rect.getWidth()/2 , point.getY() - rect.getHeight()/2).perform();
            actions().moveToElement(Selenide.$x("//canvas").getWrappedElement(),
                    - rect.getWidth()/2 , -rect.getHeight()/2).perform();
            Waiting.sleep(50);
        }
    }

    private List<Point> findNodesColor(Color c) {
        List<Point> points = new ArrayList<>();
        BufferedImage image = canvas.screenshotAsImage();
        Color[][] colors = new Color[Objects.requireNonNull(image).getWidth()][image.getHeight()];
        int w = image.getWidth();
        int h = image.getHeight();
        List<Node> nodes = new ArrayList<>();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                colors[x][y] = new Color(image.getRGB(x, y));
            }
        }

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //если нужный цвет то
                if (c.equals(colors[x][y])) {
                    //если верний пиксель этого же цвета
                    if (y > 1)
                        if (c.equals(colors[x][y - 1]))
                            continue;
                    //если левый пиксель этого же цвета
                    if (x > 1)
                        if (c.equals(colors[x - 1][y]))
                            continue;

                    int lenW = 0;
                    int n = 0;
                    //замеряем длину объекта
                    while (x + n < w && c.equals(colors[x + n][y])) {
                        lenW++;
                        n++;
                    }

                    //если длина меньше 16 то скипаем
                    if (lenW < 16)
                        continue;

                    int lenH = 0;
                    n = 0;
                    //замеряем высоту объекта
                    while (c.equals(colors[x][y + n]) && y + n < h) {
                        lenH++;
                        n++;
                    }
                    //если в объекте > 50% искомого цвета, то это наш объект
                    int count = 0;
                    for (int rX = 0; rX < lenW; rX++) {
                        for (int rY = 0; rY < lenH; rY++) {
                            if (c.equals(colors[x + rX][y + rY]))
                                count++;
                        }
                    }
                    if (lenW * lenH / count * 100 > 50) {
                        Node node = new Node(x, y, lenW, lenH, c);
                        if (nodes.stream().anyMatch(e -> isIntersect(e, node)))
                            continue;
                        nodes.add(node);
                        points.add(new Point(lenW / 2 + x, lenH / 2 + y));
                        System.out.printf("%d, %d%n", lenW / 2 + x, lenH / 2 + y);
//                        System.out.printf("%d, %d, %d, %d%n", x, y, lenW, lenH);

                        String cmd = "var ctx = document.getElementsByTagName('canvas')[0].getContext('2d');\n" +
                                "ctx.strokeStyle = \"rgb(200,0,0)\";\n" +
                                "ctx.strokeRect(%d, %d, %d, %d);";
                        executeJavaScript(
                                String.format(cmd, x, y, lenW, lenH)
                        );
                    }
                }
            }
        }
        return points;
    }

    private boolean ii(int a1, int a2, int b1, int b2) {
        return (a1 <= b2) && (b1 <= a2);
    }

    private boolean isIntersect(Node a, Node b) {
        return ii(a.getX(), a.getX() + a.getW(), b.getX(), b.getX() + b.getW()) && ii(a.getY(), a.getY() + a.getH(), b.getY(), b.getY() + b.getH());
    }

    @Data
    @AllArgsConstructor
    static class Node {
        int x, y, w, h;
        Color c;
    }
}
