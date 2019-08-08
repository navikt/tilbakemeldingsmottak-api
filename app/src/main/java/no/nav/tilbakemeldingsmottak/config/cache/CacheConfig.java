package no.nav.tilbakemeldingsmottak.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {
	public static final String USER_CACHE = "userCache";
	public static final String STS_CACHE = "stsCache";

	@Bean
    CacheManager cacheManager() {
		SimpleCacheManager manager = new SimpleCacheManager();
		manager.setCaches(Arrays.asList(
				new CaffeineCache(USER_CACHE, Caffeine.newBuilder()
						.expireAfterWrite(8, TimeUnit.HOURS)
						.maximumSize(10000)
						.build()),
				new CaffeineCache(STS_CACHE, Caffeine.newBuilder()
						.expireAfterWrite(50, TimeUnit.MINUTES)
						.maximumSize(10000)
						.build())
		));
		return manager;
	}
}
