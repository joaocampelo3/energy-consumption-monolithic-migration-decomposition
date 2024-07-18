package edu.ipp.isep.dei.dimei.retailproject.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CaffeineConfig {
    @Value("${cache.caffeine.time-to-live}")
    private int timeToLive;
    @Value("${cache.caffeine.initialCapacity}")
    private int initialCapacity;
    @Value("${cache.caffeine.maximumSize}")
    private int maximumSize;

    private static final String CATEGORIES_CACHE = "categories";
    private static final String ITEMS_CACHE = "items";
    private static final String MERCHANTS_CACHE = "merchants";
    private static final String MERCHANT_ORDERS_CACHE = "merchantOrders";
    private static final String ORDERS_CACHE = "orders";
    private static final String SHIPPING_ORDERS_CACHE = "shippingorders";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());

        // Add multiple caches
        String[] cacheNames = {
                CATEGORIES_CACHE,
                ITEMS_CACHE,
                MERCHANTS_CACHE,
                MERCHANT_ORDERS_CACHE,
                ORDERS_CACHE,
                SHIPPING_ORDERS_CACHE
        };
        cacheManager.setCacheNames(Arrays.asList(cacheNames));

        return cacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(Duration.ofMinutes(timeToLive));
    }
}
