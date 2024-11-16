package io.kraluk.buildingproxy.domain.building.entity

// Ideally it should be a rich domain object represented by a regular class, but for the sake of simplicity, it's a data class
data class Building(
  val id: Long,
  val name: String,
  val address: String,
  val floors: List<BuildingFloor>,
)

data class BuildingFloor(
  val id: Long,
  val level: Int,
  val xyGeoJsonImage: GeoJsonImage?,
  val latLongGeoJsonImage: GeoJsonImage?,
  val properties: Map<String, Any>?, // as we don't know the proper structure of the properties, it's a map
)

// GeoJson extension for Jakson can be used to serialize/deserialize GeoJson objects -> https://github.com/GeosatCO/postgis-geojson
// For the sake of simplicity, we use a map to represent GeoJson objects, as we're only passing this data through
data class GeoJsonImage(
  val type: GeoJsonImageType,
  val data: Map<String, Any>, // it should be a GeoJson object, but for the sake of simplicity, it's a map
)

enum class GeoJsonImageType {
  LAT_LONG,
  X_Y,
}

class BuildingException(override val message: String, val buildingId: Long? = null, override val cause: Throwable? = null) :
  RuntimeException(message, cause)

class BuildingHasTooManyFloorsException(override val message: String) : RuntimeException(message)