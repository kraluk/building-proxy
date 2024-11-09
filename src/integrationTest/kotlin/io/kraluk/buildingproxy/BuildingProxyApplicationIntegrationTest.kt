package io.kraluk.buildingproxy

import io.kraluk.buildingproxy.test.IntegrationTest
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class BuildingProxyApplicationIntegrationTest : IntegrationTest() {

  @Autowired
  private lateinit var environment: Environment

  @Test
  fun `should have integration profile active`() {
    // When
    val profiles = environment.activeProfiles

    // Then
    profiles `should contain` "integration"
  }
}