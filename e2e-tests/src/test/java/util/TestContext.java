package util;

import java.util.HashMap;
import java.util.Map;

import static java.lang.ThreadLocal.withInitial;

public enum TestContext {

    CONTEXT;

    private final ThreadLocal<Map<String, Object>> testContexts = withInitial(HashMap::new);

    public <T> T get(String name) {
        return (T) testContexts.get()
                .get(name);
    }

    public <T> void set(String name, T object) {
        testContexts.get()
                .put(name, object);
    }

    public void reset() {
        testContexts.get()
                .clear();
    }
}

