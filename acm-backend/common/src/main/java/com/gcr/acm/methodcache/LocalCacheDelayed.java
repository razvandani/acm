package com.gcr.acm.methodcache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class LocalCacheDelayed implements Delayed {
    private String cacheKey;
    private long expirationTimestamp;

    public LocalCacheDelayed(String cacheKey, long delay) {
        this.cacheKey = cacheKey;
        this.expirationTimestamp = System.currentTimeMillis() + delay;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = expirationTimestamp - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.expirationTimestamp < ((LocalCacheDelayed) o).expirationTimestamp) {
            return -1;
        }
        if (this.expirationTimestamp > ((LocalCacheDelayed) o).expirationTimestamp) {
            return 1;
        }
        return 0;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public String toString() {
        return "{" +
                "cacheKey='" + cacheKey + '\'' +
                ", expirationTimestamp=" + expirationTimestamp +
                '}';
    }
}

