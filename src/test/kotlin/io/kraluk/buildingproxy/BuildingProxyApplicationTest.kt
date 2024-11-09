package io.kraluk.buildingproxy

import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test

class BuildingProxyApplicationTest {

  @Test
  fun `should check if true is true`() {
    // When
    val result = true

    // Then
    result.`should be true`()
  }
}