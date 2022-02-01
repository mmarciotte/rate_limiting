package com.mm.rate_limit.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiter Service
 * Validate access based on the configuration of "maximum number of accesses" in a "time window".
 * "maximum number of accesses" : rate-limiter.request-allowed configuration property
 * "time window" : rate-limiter.refresh-seconds configuration property
 */
@Service
public class RateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    private final MeterRegistry meterRegistry;

    private final StringRedisTemplate stringTemplate;

    @Value("${rate-limiter.request-allowed}")
    private int requestAllowed;
    @Value("${rate-limiter.refresh-seconds}")
    private int refreshSeconds;

    public RateLimiter(MeterRegistry meterRegistry, StringRedisTemplate stringTemplate) {
        this.meterRegistry = meterRegistry;
        this.stringTemplate = stringTemplate;
    }

    /**
     * Validate if key is allowed according rate limiting configuration
     *
     * @param key identifier
     * @return boolean is allowed result
     */
    public boolean isAllowed(String key) {

        boolean isAllowed = false;

        try {
            List<Object> txResults = stringTemplate.execute(new SessionCallback<>() {
                @Override
                public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
                    final StringRedisTemplate redisTemplate = (StringRedisTemplate) operations;
                    final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
                    operations.multi();
                    valueOperations.increment(key);
                    redisTemplate.expire(key, refreshSeconds, TimeUnit.SECONDS);
                    return operations.exec();
                }
            });
            assert txResults != null;
            isAllowed = (txResults.size() > 0 && (Long) txResults.get(0) <= requestAllowed);
            logger.info("User: " + key + " Current request count: " + txResults.get(0) + " allowed: " + isAllowed);
            resultMetrics(key, isAllowed);
        } catch (final Throwable t) {
            logger.warn("Error on redis operation: {}", t.getMessage());
        }
        return isAllowed;
    }

    /**
     * metrics collector
     */
    private void resultMetrics(String key, boolean isAllowed) {
        Counter.builder("service.rate-limiter.allowed." + isAllowed)
                .tag("key", key)
                .description("Allowed counter")
                .register(meterRegistry).increment();
    }

}
