package com.mm.rate_limit.service;

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

    private final StringRedisTemplate stringTemplate;

    @Value("${rate-limiter.request-allowed}")
    private int requestAllowed;
    @Value("${rate-limiter.refresh-seconds}")
    private int refreshSeconds;

    public RateLimiter(StringRedisTemplate stringTemplate) {
        this.stringTemplate = stringTemplate;
    }

    public boolean isAllowed(String key) {

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
        logger.info("User: " + key + " Current request count: " + txResults.get(0));
        if (txResults.size() > 0 && (Long) txResults.get(0) > requestAllowed) {
            logger.info("denied");
            return false;
        }
        logger.info("allowed");
        return true;
    }

}
