package ui.t1.pages.S3Storage.IncompleteDownloads;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import lombok.NoArgsConstructor;
import ui.elements.DataTable;
import ui.elements.Table;
import ui.t1.pages.S3Storage.AbstractLayerS3;

@NoArgsConstructor
public class IncompleteDownloadsLayer extends AbstractLayerS3<IncompleteDownloadsLayer> {

    private DataTable lifeCycleList;

    public IncompleteDownloadsLayer(String name)
    {
        super(name);
    }

    public void checkIncompleteDownloadFileIsAppear(String text){
        new Table(Selenide.$x("(.//table[@class=\"MuiTable-root\"])[2]"))
                .getRow(0).getElementByColumn("Название")
                .shouldHave(Condition.text(text));
    }
}
