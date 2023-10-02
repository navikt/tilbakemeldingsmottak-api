package no.nav.tilbakemeldingsmottak.config.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration
class CacheConfig {
    companion object {
        const val NORG2_CACHE = "norgCache"
    }

    @Bean
    fun norg2CacheManager(): CacheManager {
        val manager = SimpleCacheManager()
        manager.setCaches(
            listOf(
                CaffeineCache(
                    NORG2_CACHE, Caffeine.newBuilder()
                        .expireAfterWrite(50, TimeUnit.MINUTES)
                        .maximumSize(10000)
                        .build()
                )
            )
        )
        return manager
    }

    @Bean
    fun caffeineConfig(): Caffeine<*, *> {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
    }
}
