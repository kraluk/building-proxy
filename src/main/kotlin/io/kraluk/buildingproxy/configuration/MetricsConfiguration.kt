package io.kraluk.buildingproxy.configuration

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfiguration {

  @Bean
  fun timedAspect(registry: MeterRegistry): TimedAspect =
    TimedAspect(registry)
}