package io.kraluk.buildingproxy.adapter.building.repository

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.entity.BuildingFloor
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImage
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImageType

interface KontaktBuildingClient {
  fun findById(id: Long): KontaktBuildings
}

/**
 * Based on the information provided in the
 * [API docs](https://developer.kontakt.io/docs/dev-ctr-loc-occ-api/c2466a130e6b4-list-all-buildings)
 *
 * Additionally, we're assuming that fields marked in the docs as '>= 1 characters':
 * - are not null
 * - are not empty/blank
 * - have some meaningful value
 *
 * Assumptions for collections:
 * - if there are no elements in a list, it's an empty list returned, not `null`
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class KontaktBuildings(
  val content: List<KontaktBuilding>,
  val page: KontaktPage,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KontaktPage(
  val totalElements: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KontaktBuilding(
  val id: Long,
  val name: String,
  val address: String,
  val floors: List<KontaktFloor>, // assumption: if there are no floors, it's an empty list
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KontaktFloor(
  val id: Long,
  val name: String,
  val level: Int,
  // it should be a GeoJson object, but for the sake of simplicity, it's a map as we only pass this data
  val imageLatLngGeojson: Map<String, Any>?,
  val imageXyGeojson: Map<String, Any>?,
  // it's quite generic as it's quite hard to design a proper model without knowing the actual data that is not provided in docs
  val properties: Map<String, Any>?,
)

fun KontaktBuilding.toDomain() =
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