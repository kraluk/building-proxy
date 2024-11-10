package io.kraluk.buildingproxy.shared.contract.http

import io.kraluk.buildingproxy.test.entity.building
import io.kraluk.buildingproxy.test.entity.floor
import io.kraluk.buildingproxy.test.entity.geoJsonImage
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should not be null`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class BuildingHttpTest {

  @Test
  fun `should map from domain object`() {
    // Given
    val domain = building(
      floors = listOf(
        floor(level = 1, xyGeoJsonImage = geoJsonImage(), latLongGeoJsonImage = null),
        floor(level = 2, xyGeoJsonImage = null, latLongGeoJsonImage = geoJsonImage()),
      ),
    )

    // When
    val http = domain.toHttp()

    // Then
    http.name `should be equal to` domain.name
    http.address `should be equal to` domain.address
    http.floors shouldHaveSize domain.floors.size

    // And then floor 1 is mapped properly
    http.floors[0].level `should be equal to` domain.floors[0].level
    http.floors[0].xyGeoJsonImage.`should not be null`()
    http.floors[0].xyGeoJsonImage?.type?.name `should be equal to` domain.floors[0].xyGeoJsonImage?.type?.name
    http.floors[0].xyGeoJsonImage?.data `should be equal to` domain.floors[0].xyGeoJsonImage?.data
    http.floors[0].latLongGeoJsonImage.`should be null`()
    http.floors[0].properties `should be equal to` domain.floors[0].properties

    // And then floor 2 is mapped properly
    http.floors[1].level `should be equal to` domain.floors[1].level
    http.floors[1].xyGeoJsonImage.`should be null`()
    http.floors[1].latLongGeoJsonImage.`should not be null`()
    http.floors[1].latLongGeoJsonImage?.type?.name `should be equal to` domain.floors[0].xyGeoJsonImage?.type?.name
    http.floors[1].latLongGeoJsonImage?.data `should be equal to` domain.floors[0].xyGeoJsonImage?.data
    http.floors[1].properties `should be equal to` domain.floors[1].properties
  }
}