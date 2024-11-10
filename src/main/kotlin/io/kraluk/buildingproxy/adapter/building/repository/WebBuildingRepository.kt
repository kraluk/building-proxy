package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.entity.BuildingException
import io.kraluk.buildingproxy.domain.building.entity.BuildingFloor
import io.kraluk.buildingproxy.domain.building.entity.BuildingHasTooManyFloorsException
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImage
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImageType
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import io.kraluk.buildingproxy.shared.logger
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException

@Component
class WebBuildingRepository(
  private val client: KontaktBuildingClient,
) : BuildingRepository {
  override fun findById(id: Long): Building? =
    try {
      client.findById(id)
        .also {
          // maybe it should not be responsibility of the repository to handle this kind of logic...
          if (it.page.totalElements != 1) {
            throw BuildingHasTooManyFloorsException("There is more than one building per single id '$id'! - '$it'")
          }
        }
        .content
        .firstOrNull()
        ?.toDomain()
        ?: null.also { log.warn("Building with id '{}' not found", id) }
    } catch (e: HttpStatusCodeException) { // perhaps this should be handled in a more not-so-generic way
      if (e.statusCode.isNotFound()) {
        log.warn("Building with id '{}' not found", id)
        null
      } else {
        log.error("Error while fetching building with id '{}'", id, e)
        throw BuildingException("Error while fetching building with id '$id'", e)
      }
    }

  companion object {
    private val log by logger()

    private fun HttpStatusCode.isNotFound() =
      this == HttpStatusCode.valueOf(404)

    private fun KontaktBuilding.toDomain() =
      Building(
        id = id,
        name = name,
        address = address,
        floors = floors.map { it.toDomain() },
      )

    private fun KontaktFloor.toDomain() =
      BuildingFloor(
        id = id,
        level = level,
        xyGeoJsonImage = imageXyGeojson?.let { GeoJsonImage(type = GeoJsonImageType.X_Y, data = imageXyGeojson.toMap()) },
        latLongGeoJsonImage = imageLatLngGeojson?.let { GeoJsonImage(type = GeoJsonImageType.LAT_LONG, data = imageLatLngGeojson.toMap()) },
        properties = properties,
      )
  }
}