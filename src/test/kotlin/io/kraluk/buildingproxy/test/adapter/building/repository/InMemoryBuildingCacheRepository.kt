package io.kraluk.buildingproxy.test.adapter.building.repository

import io.kraluk.buildingproxy.adapter.building.repository.BuildingCacheRepository
import io.kraluk.buildingproxy.domain.building.entity.Building
import java.util.concurrent.ConcurrentHashMap

class InMemoryBuildingCacheRepository : BuildingCacheRepository {
  private val elements = ConcurrentHashMap<Long, Building>()

  override fun findById(id: Long): Building? =
    if (id == THROWING_FIND_ID) {
      throwCacheException()
    } else {
      elements[id]
    }

  override fun save(building: Building) {
    if (building.id == THROWING_SAVE_ID) {
      throwCacheException()
    } else {
      elements.put(building.id, building)
        .let { building }
    }
  }

  fun elements(): List<Building> = elements.values.toList()

  companion object {
    const val THROWING_FIND_ID = 666L
    const val THROWING_SAVE_ID = 999L

    private fun throwCacheException(): Building {
      throw IllegalStateException("CACHE ERROR")
    }
  }
}