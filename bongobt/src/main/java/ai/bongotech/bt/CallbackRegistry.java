package ai.bongotech.bt;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class CallbackRegistry {
    // Jubayer Hossain @www.bongotech.ai
    interface Callback { void onGranted(); void onDenied(boolean neverAskAgain); }
    private static final Map<String, Callback> MAP = new ConcurrentHashMap<>();

    static String put(Callback cb) {
        String id = UUID.randomUUID().toString();
        MAP.put(id, cb); return id;
    }

    static void consume(String id, boolean granted, boolean naa) {
        Callback cb = MAP.remove(id);
        if (cb == null) return;
        if (granted) cb.onGranted(); else cb.onDenied(naa);
    }
}
