package io.kraluk.buildingproxy.configuration

import io.kraluk.buildingproxy.shared.logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneOffset

@Configuration
class ClockConfiguration {

  @Bean
  fun clock(): Clock =
    Clock.system(TIME_ZONE)
      .also { log.info("Using Clock with the time zone - '{}'", TIME_ZONE) }

  companion object {
    private val TIME_ZONE = ZoneOffset.UTC

    private val log by logger()
  }
}