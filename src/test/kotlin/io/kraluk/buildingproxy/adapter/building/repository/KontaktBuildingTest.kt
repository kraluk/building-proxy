package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImage
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImageType
import io.kraluk.buildingproxy.test.adapter.building.repository.kontaktBuilding
import io.kraluk.buildingproxy.test.adapter.building.repository.kontaktFloor
import io.kraluk.buildingproxy.test.adapter.building.repository.kontaktGeojsonImage
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should not be null`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class KontaktBuildingTest {

  @Test
  fun `should map to domain object`() {
    // Given
    val kontakt: KontaktBuilding = kontaktBuilding(
      floors = listOf(
        kontaktFloor(level = 1, imageXyGeojson = kontaktGeojsonImage(), imageLatLngGeojson = null),
        kontaktFloor(level = 2, imageXyGeojson = null, imageLatLngGeojson = kontaktGeojsonImage())
      )
    )

    // When
    val domain: Building = kontakt.toDomain()

    // Then
    domain.id `should be equal to` kontakt.id
    domain.name `should be equal to` kontakt.name
    domain.address `should be equal to` kontakt.address

    domain.floors.shouldHaveSize(kontakt.floors.size)

    // And then first floor mapping is checked
    domain.floors[0].id `should be equal to` kontakt.floors[0].id
    domain.floors[0].level `should be equal to` kontakt.floors[0].level
    domain.floors[0].xyGeoJsonImage.`should not be null`() `should be equal to` GeoJsonImage(
      type = GeoJsonImageType.X_Y,
      data = kontakt.floors[0].imageXyGeojson.`should not be null`()
    )
    domain.floors[0].latLongGeoJsonImage.`should be null`()
    domain.floors[0].properties `should be equal to` kontakt.floors[0].properties

    // And then second floor mapping is checked
    domain.floors[1].id `should be equal to` kontakt.floors[1].id
    domain.floors[1].level `should be equal to` kontakt.floors[1].level
    domain.floors[1].xyGeoJsonImage.`should be null`()
    domain.floors[1].latLongGeoJsonImage.`should not be null`() `should be equal to` GeoJsonImage(
      type = GeoJsonImageType.LAT_LONG,
      data = kontakt.floors[1].imageLatLngGeojson.`should not be null`()
    )
    domain.floors[1].properties `should be equal to` kontakt.floors[1].properties
  }
}