package ui.elements;

import com.codeborne.selenide.*;
import core.utils.Waiting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.*;
import static core.helper.StringUtils.$x;

public class Graph implements TypifiedElement {
    private final SelenideElement canvas = $x("//canvas");
    private final SelenideElement btnZoomUp = $x("(//canvas/../..//button)[1]");
    private final SelenideElement btnZoomDown = $x("(//canvas/../..//button)[2]");
    private final SelenideElement btnFullScreen = $x("(//h2//button)[1]");
    private final SelenideElement btnCloseWindow = $x("(//h2//button)[2]");
    private final SelenideElement statusList = $x("(//div[contains(.,'Не начато') and contains(.,'Запущено')])[last()]");
    private final ElementsCollection toolTip = $$x("//canvas/following-sibling::*/*/div");

    boolean throwSkipNodes, throwRunNodes, throwNotRunNodes;
    private List<Point> lastNodesList = new ArrayList<>();

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

    private boolean updateCurrentNodeCoordinates() {
        List<Point> points = new ArrayList<>();
        for (Color c : colors)
            points.addAll(findNodesColor(c));
        if (lastNodesList.equals(points))
            return false;
        lastNodesList = points;
        return true;
    }

    private void checkCurrentNodes() {
        Rectangle rect = canvas.getRect();
        if(!updateCurrentNodeCoordinates())
            return;
        for (Point point : lastNodesList) {
            actions().moveToElement(canvas.getWrappedElement(), point.getX() - rect.getWidth() / 2, point.getY() - rect.getHeight() / 2).perform();
            List<String> texts = toolTip.shouldBe(CollectionCondition.size(3)).texts();

            //Проверяем что все узлы без ошибок
            Assertions.assertNotEquals("Ошибка", texts.get(2));

            if (throwSkipNodes)
                Assertions.assertNotEquals("Пропущено и завершено", texts.get(2));

            if (throwRunNodes)
                Assertions.assertNotEquals("Запущено", texts.get(2));

            if (throwNotRunNodes)
                Assertions.assertNotEquals("Не начато", texts.get(2));

            actions().moveToElement(canvas.getWrappedElement(), -rect.getWidth() / 2, -rect.getHeight() / 2).perform();
            Waiting.sleep(50);
        }
    }

    public void checkGraph() {
        btnFullScreen.shouldBe(Condition.enabled).click();
        executeJavaScript("arguments[0].style.display = 'none'", statusList);
        Rectangle rect = canvas.getRect();
        for (int i = 0; i < 11; i++)
            btnZoomDown.shouldBe(Condition.enabled).click();
        //Смещаем граф вверх и смотрим что там
        dragAndDrop(0, -rect.getHeight() / 2 + 50, 0, rect.getHeight() / 2);
        checkCurrentNodes();
        //влево
        dragAndDrop(-rect.getWidth() / 2, 0, rect.getWidth() / 2 - 50, 0);
        checkCurrentNodes();
        //вниз
        dragAndDrop(0, rect.getHeight() / 2, 0, -rect.getHeight() / 2 + 50);
        checkCurrentNodes();
        //и вправо
        dragAndDrop(rect.getWidth() / 2 - 50, 0, -rect.getWidth() / 2, 0);
        checkCurrentNodes();
        btnCloseWindow.shouldBe(Condition.enabled).click();
    }

    private void dragAndDrop(int x1, int y1, int x2, int y2) {
        actions().clickAndHold(canvas.getWrappedElement())
                .moveToElement(canvas.getWrappedElement(), x1, y1)
                .moveToElement(canvas.getWrappedElement(), x2, y2)
                .release()
                .perform();
        canvas.click();
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
                    //если верхний пиксель этого же цвета
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

    public Graph throwSkipNodes() {
        throwSkipNodes = true;
        return this;
    }

    public Graph throwRunNodes() {
        throwRunNodes = true;
        return this;
    }

    public Graph throwNotRunNodes() {
        throwNotRunNodes = true;
        return this;
    }

    @Data
    @AllArgsConstructor
    static class Node {
        int x, y, w, h;
        Color c;
    }
}
