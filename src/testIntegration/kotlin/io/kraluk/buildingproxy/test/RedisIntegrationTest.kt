package io.kraluk.buildingproxy.test

import io.kraluk.buildingproxy.test.extension.ClearCacheExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@Import(RedisTestConfiguration::class)
@TestPropertySource(
  properties = [
    "spring.data.redis.enabled=true",
    "app.building.cache.enabled=true",
  ],
)
@ExtendWith(ClearCacheExtension::class)
class RedisIntegrationTest : IntegrationTest()

@TestConfiguration(proxyBeanMethods = false)
class RedisTestConfiguration {

  @Bean
  @ServiceConnection(name = "redis")
  fun redisContainer(): GenericContainer<*> =
    GenericContainer("redis:8.2.1-alpine")
      .withExposedPorts(6379)
      .withReuse(true)
}