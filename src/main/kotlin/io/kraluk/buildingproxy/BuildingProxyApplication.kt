package io.kraluk.buildingproxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BuildingProxyApplication

fun main(args: Array<String>) {
  runApplication<BuildingProxyApplication>(*args)
}