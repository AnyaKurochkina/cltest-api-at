package ui.t1.pages.S3Storage.IncompleteDownloads;

import ui.elements.DataTable;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class IncompleteDownloadsLayer extends AbstractLayerS3<IncompleteDownloadsLayer> {

    private DataTable lifeCycleList;

    public IncompleteDownloadsLayer(String name)
    {
        super(name);
    }

    public IncompleteDownloadsLayer()
    {

    }


}
