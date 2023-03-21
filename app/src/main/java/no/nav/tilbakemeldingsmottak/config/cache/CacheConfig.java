package no.nav.tilbakemeldingsmottak.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {
	public static final String AKTOER_CACHE = "aktoerCache";
	public static final String NORG2_CACHE = "norgCache";

	@Bean
    CacheManager cacheManager() {
		SimpleCacheManager manager = new SimpleCacheManager();
		manager.setCaches(Arrays.asList(
				new CaffeineCache(AKTOER_CACHE, Caffeine.newBuilder()
						.expireAfterWrite(50, TimeUnit.MINUTES)
						.maximumSize(10000)
						.build()),
				new CaffeineCache(NORG2_CACHE, Caffeine.newBuilder()
						.expireAfterWrite(50, TimeUnit.MINUTES)
						.maximumSize(10000)
						.build())
		));
		return manager;
	}

	@Bean
	public CacheManager cacheManager(Caffeine caffeine) {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeine);
		return caffeineCacheManager;
	}

	@Bean
	public Caffeine caffeineConfig() {
		return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES);
	}
}
