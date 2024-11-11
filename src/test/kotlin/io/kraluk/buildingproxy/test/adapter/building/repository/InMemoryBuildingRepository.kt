package io.kraluk.buildingproxy.test.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryBuildingRepository : BuildingRepository {
  private val elements = ConcurrentHashMap<Long, Building>()

  override fun findById(id: Long): Building? =
    elements[id]

  fun save(building: Building) {
    elements.put(building.id, building)
      .let { building }
  }

  fun elements(): List<Building> = elements.values.toList()
}