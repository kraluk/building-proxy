package io.kraluk.buildingproxy.test.shared.contract.http

import io.kraluk.buildingproxy.shared.contract.http.BuildingFloorHttp
import io.kraluk.buildingproxy.shared.contract.http.BuildingHttp
import io.kraluk.buildingproxy.shared.contract.http.GeoJsonImageHttp
import io.kraluk.buildingproxy.shared.contract.http.GeoJsonImageTypeHttp

fun buildingHttp(
  name: String = "Empire State Building",
  address: String = "20 W 34th St., New York, NY 10001, USA",
  floors: List<BuildingFloorHttp>,
): BuildingHttp = BuildingHttp(
  name = name,
  address = address,
  floors = floors,
)

fun floorHttp(
  level: Int = 1,
  xyGeoJsonImage: GeoJsonImageHttp?,
  latLongGeoJsonImage: GeoJsonImageHttp?,
  properties: Map<String, Any> = mapOf("key" to "value", "key2" to mapOf("key" to "value")),
): BuildingFloorHttp = BuildingFloorHttp(
  level = level,
  xyGeoJsonImage = xyGeoJsonImage,
  latLongGeoJsonImage = latLongGeoJsonImage,
  properties = properties,
)

fun geoJsonImageHttp(
  type: GeoJsonImageTypeHttp = GeoJsonImageTypeHttp.X_Y,
  data: Map<String, Any> = mapOf("x" to 0.0, "y" to 0.0),
): GeoJsonImageHttp = GeoJsonImageHttp(
  type = type,
  data = data,
)