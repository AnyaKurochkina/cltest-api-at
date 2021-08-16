package models.orderService.interfaces;

import core.utils.Waiting;
import lombok.AllArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
public class IProductMock extends IProduct {
    public void order(){
        Waiting.sleep(1000);
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        return null;
    }

    public void restart(){}
    public void stopHard(){}
    public void stopSoft(){}
    public void start(){}
    public void delete(){}
    public void resize() {}
    public void expandMountPoint() {}
    public void runActionsBeforeOtherTests(){}
    public void runActionsAfterOtherTests() {
        Waiting.sleep(1000);
    }

    String str;

    @Override
    public String toString(){
        return str;
    }
}
