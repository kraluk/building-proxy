package io.kraluk.buildingproxy.shared.contract.http

import io.kraluk.buildingproxy.domain.building.entity.Building as BuildingDomain
import io.kraluk.buildingproxy.domain.building.entity.BuildingFloor as BuildingFloorDomain
import io.kraluk.buildingproxy.domain.building.entity.GeoJsonImage as GeoJsonImageDomain

// This is a contract class that represents the HTTP response of the Building entity, but at some point, it can be omitted
// and the domain class can be used directly in the controller layer to avoid boilerplate code and the additional mappings.
// Sure, it won't super-clean solution but if the performance is a concern, it can be, let's say, a good trade-off.
data class BuildingHttp(
  val name: String,
  val address: String,
  val floors: List<BuildingFloorHttp>,
)

data class BuildingFloorHttp(
  val level: Int,
  val xyGeoJsonImage: GeoJsonImageHttp?,
  val latLongGeoJsonImage: GeoJsonImageHttp?,
  val properties: Map<String, Any>?,
)

data class GeoJsonImageHttp(
  val type: GeoJsonImageTypeHttp,
  val data: Map<String, Any>, // it should be a GeoJson object, but for the sake of simplicity, it's a map
)

enum class GeoJsonImageTypeHttp {
  LAT_LONG,
  X_Y,
}

fun BuildingDomain.toHttp(): BuildingHttp =
  BuildingHttp(
    name = this.name,
    address = this.address,
    floors = this.floors.map { it.toHttp() },
  )

private fun BuildingFloorDomain.toHttp(): BuildingFloorHttp =
  BuildingFloorHttp(
    level = this.level,
    xyGeoJsonImage = this.xyGeoJsonImage?.toHttp(),
    latLongGeoJsonImage = this.latLongGeoJsonImage?.toHttp(),
    properties = this.properties,
  )

private fun GeoJsonImageDomain.toHttp(): GeoJsonImageHttp =
  GeoJsonImageHttp(
    type = GeoJsonImageTypeHttp.valueOf(this.type.name),
    data = this.data.toMap(),
  )