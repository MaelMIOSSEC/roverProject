package EventHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventHandler {

    Map<Class<? extends AbstractEvent>,Set<Object>> registry = new HashMap<Class<? extends AbstractEvent>,Set<Object>>();

    public void send (AbstractEvent event) {
        Set<Object> l = registry.get(event.getClass());
        if (l == null) return;
        for (Object o : l) {
            event.sendTo(o);
        }
    }

    public void registerListener(Class<? extends AbstractEvent> whichEventType, Object listener) {
        Set<Object> set = registry.computeIfAbsent(whichEventType, k -> new HashSet<Object>());
        set.add(listener);
    }

    public void unregisterListener(Class<? extends AbstractEvent> whichEventType, Object listener) {
        Set<Object> set = registry.get(whichEventType);
        if (set == null) return;
        set.remove(listener);
    }
}
