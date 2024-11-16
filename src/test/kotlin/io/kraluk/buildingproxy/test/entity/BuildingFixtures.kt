package io.kraluk.buildingproxy.test.entity

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.entity.BuildingFloor
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImage
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImageType

fun building(
  id: Long = 1L,
  name: String = "Empire State Building",
  address: String = "20 W 34th St., New York, NY 10001, USA",
  floors: List<BuildingFloor> = listOf(floor()),
): Building = Building(
  id = id,
  name = name,
  address = address,
  floors = floors,
)

@Suppress("LongParameterList")
fun floor(
  id: Long = 1L,
  level: Int = 1,
  xyGeoJsonImage: GeoJsonImage? = geoJsonImage(),
  latLongGeoJsonImage: GeoJsonImage? = null,
  properties: Map<String, Any> = mapOf("key" to "value", "key2" to mapOf("key" to "value")),
): BuildingFloor = BuildingFloor(
  id = id,
  level = level,
  xyGeoJsonImage = xyGeoJsonImage,
  latLongGeoJsonImage = latLongGeoJsonImage,
  properties = properties,
)

fun geoJsonImage(
  type: GeoJsonImageType = GeoJsonImageType.X_Y,
  data: Map<String, Any> = mapOf("x" to 0.0, "y" to 0.0),
): GeoJsonImage = GeoJsonImage(
  type = type,
  data = data,
)