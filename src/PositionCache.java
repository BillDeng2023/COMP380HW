import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PositionCache {
    private final LoadingCache<Integer, Pair<String, Double>> cache;

    public PositionCache() {
        cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<Integer, Pair<String, Double>>() {
                @Override
                public Pair<String, Double> load(Integer key) throws Exception {
                    return loadValue(key); // Load value for given key
                }
            });
    }

    public Pair<String, Double> getValue(Integer key) {
        return cache.getUnchecked(key); // Get value for given key from cache
    }

    public boolean hasKey(Integer key) {
        return cache.asMap().containsKey(key);
    }

    public void putValue(Integer positionHash, String action, Double score) {
        Pair<String, Double> value = new Pair<>(action, score);

        Pair<String, Double> currentValue = cache.getIfPresent(positionHash);
        if (currentValue == null || value.getScore() > currentValue.getScore()) {
            // Key not present in cache
            // Put new value for given key into cache
            cache.put(positionHash, value);
        }
    }

    private Pair<String, Double> loadValue(Integer key) {
        // Load value for given key from external source
        // For example, from a database or remote service
        return null;
    }

    public static class Pair<A, S> {
        private final A action;
        private final S score;

        public Pair(A action, S score) {
            this.action = action;
            this.score = score;
        }

        public A getAction() {
            return this.action;
        }

        public S getScore() {
            return this.score;
        }
    }
}