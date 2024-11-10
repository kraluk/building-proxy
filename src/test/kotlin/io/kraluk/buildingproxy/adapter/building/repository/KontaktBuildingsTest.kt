package io.kraluk.buildingproxy.adapter.building.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kraluk.buildingproxy.test.contentOf
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class KontaktBuildingsTest {

  @Test
  fun `should deserialize kontakt io's building raw response to data model`() {
    // Given
    val json = contentOf("building/kontakt/buildings-response.json")

    // When
    val buildings: KontaktBuildings = mapper.readValue(json)

    // Then
    buildings.content.shouldHaveSize(1)
  }

  companion object {
    private val mapper = jacksonObjectMapper()
  }
}