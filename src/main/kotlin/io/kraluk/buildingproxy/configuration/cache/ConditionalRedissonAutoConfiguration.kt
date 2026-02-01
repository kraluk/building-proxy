package io.kraluk.buildingproxy.configuration.cache

import org.redisson.Redisson
import org.redisson.spring.starter.RedissonAutoConfigurationV4
import org.redisson.spring.starter.RedissonProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisOperations

/**
 * There is no easy way to disable Redisson's autoconfiguration logic via property. Therefore, a simple workaround is needed.
 *
 * @see <a href="https://github.com/redisson/redisson/issues/3165">issue</a>
 * @see <a href="https://redisson.org/docs/integration-with-spring/#faq">docs</a>
 * @see [io.kraluk.buildingproxy.BuildingProxyApplication]
 */
@Configuration
@AutoConfiguration(before = [DataRedisAutoConfiguration::class])
@ConditionalOnClass(Redisson::class, RedisOperations::class, DataRedisAutoConfiguration::class)
@EnableConfigurationProperties(RedissonProperties::class, DataRedisProperties::class)
@ConditionalOnProperty(name = ["spring.data.redis.enabled"], havingValue = "true", matchIfMissing = true)
class ConditionalRedissonAutoConfiguration : RedissonAutoConfigurationV4()