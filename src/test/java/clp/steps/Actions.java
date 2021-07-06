package clp.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class Actions {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceSteps.class);
    private Map<String, String> actions = new HashMap<>();

    public String getTypeByAction(String action) {
        log.info("Получение type экшена для получения item_id");
        actions.put("start_vm", "vm");
        actions.put("reset_vm", "vm");
        actions.put("stop_vm_soft", "vm");
        actions.put("stop_vm_hard", "vm");
        actions.put("delete", "vm");
        actions.put("delete_two_layer", "app");

        return actions.get(action);

    }
}
