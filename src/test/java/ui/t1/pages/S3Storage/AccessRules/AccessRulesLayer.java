package ui.t1.pages.S3Storage.AccessRules;

import ui.elements.DataTable;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class AccessRulesLayer extends AbstractLayerS3<AccessRulesLayer> {

    private DataTable accessRulesList;

    public AccessRulesLayer(String name)
    {
        super(name);
    }

    public AccessRulesLayer()
    {

    }


}
