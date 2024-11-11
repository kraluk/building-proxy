package io.kraluk.buildingproxy.test.adapter.building.repository

import io.kraluk.buildingproxy.adapter.building.repository.KontaktBuilding
import io.kraluk.buildingproxy.adapter.building.repository.KontaktBuildings
import io.kraluk.buildingproxy.adapter.building.repository.KontaktFloor
import io.kraluk.buildingproxy.adapter.building.repository.KontaktPage
import kotlin.random.Random

fun kontaktBuildings(
  content: List<KontaktBuilding> = listOf(kontaktBuilding()),
  page: KontaktPage = kontaktPage(),
): KontaktBuildings =
  KontaktBuildings(
    content = content,
    page = page,
  )

fun kontaktPage(
  totalElements: Int = 1,
): KontaktPage =
  KontaktPage(
    totalElements = totalElements,
  )

fun kontaktBuilding(
  id: Long = Random.nextLong(),
  name: String = "Pałac Kultury i Nauki",
  address: String = "plac Defilad 1, 00-901 Warszawa, Polska",
  floors: List<KontaktFloor> = listOf(kontaktFloor()),
): KontaktBuilding =
  KontaktBuilding(
    id = id,
    name = name,
    address = address,
    floors = floors,
  )

@Suppress("LongParameterList")
fun kontaktFloor(
  id: Long = Random.nextLong(),
  name: String = "Super floor",
  level: Int = 1,
  imageLatLngGeojson: Map<String, Any>? = mapOf("type" to "FeatureCollection", "coordinates" to emptyList<Any>()),
  imageXyGeojson: Map<String, Any>? = mapOf("type" to "Point", "coordinates" to listOf("0" to "0", "1" to "1")),
  properties: Map<String, Any>? = mapOf("property" to "value"),
): KontaktFloor =
  KontaktFloor(
    id = id,
    name = name,
    level = level,
    imageLatLngGeojson = imageLatLngGeojson,
    imageXyGeojson = imageXyGeojson,
    properties = properties,
  )

fun kontaktGeojsonImage(): Map<String, Any> =
  mapOf("type" to "Point", "coordinates" to listOf("0" to "0", "1" to "1"))
