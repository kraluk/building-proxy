package io.kraluk.buildingproxy.configuration

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZoneOffset

class ClockConfigurationTest {

  private val configuration = ClockConfiguration()

  @Test
  fun `should create clock using UTC timezone`() {
    // When
    val clock = configuration.clock()

    // Then
    clock.zone `should be equal to` ZoneOffset.UTC
  }
}