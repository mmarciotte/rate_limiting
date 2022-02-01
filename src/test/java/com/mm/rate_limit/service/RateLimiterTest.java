package com.mm.rate_limit.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@RestClientTest(RateLimiter.class)
@Import({MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class})
public class RateLimiterTest {

    @Autowired
    private RateLimiter rateLimiter;

    @MockBean
    private StringRedisTemplate stringTemplate;

    @Test
    public void isAllowed_Ok() {
        List<Long> txResults = Arrays.asList(1L, 2L);

        when(stringTemplate.execute(ArgumentMatchers.<SessionCallback<List<Long>>>any())).thenReturn(txResults);
        assertTrue(rateLimiter.isAllowed("1"));
    }

    @Test
    public void isAllowed_Fail() {
        List<Long> txResults = Arrays.asList(6L, 7L);

        when(stringTemplate.execute(ArgumentMatchers.<SessionCallback<List<Long>>>any())).thenReturn(txResults);
        assertFalse(rateLimiter.isAllowed("1"));
    }
}
