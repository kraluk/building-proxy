package io.kraluk.buildingproxy.test.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryBuildingRepository : BuildingRepository {
  private val elements = CopyOnWriteArrayList<Building>()

  override fun findById(id: Long): Building? =
    elements.find { it.id == id }

  fun save(building: Building): Building =
    building.also { elements.add(it) }

  fun elements(): List<Building> = elements.toList()
}