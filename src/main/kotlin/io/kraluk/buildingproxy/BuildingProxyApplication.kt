package io.kraluk.buildingproxy

import io.kraluk.buildingproxy.configuration.cache.ConditionalRedissonAutoConfiguration
import org.redisson.spring.starter.RedissonAutoConfigurationV2
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(
  exclude = [RedissonAutoConfigurationV2::class],
)
@Import(ConditionalRedissonAutoConfiguration::class)
@ConfigurationPropertiesScan
class BuildingProxyApplication

fun main(args: Array<String>) {
  runApplication<BuildingProxyApplication>(*args)
}