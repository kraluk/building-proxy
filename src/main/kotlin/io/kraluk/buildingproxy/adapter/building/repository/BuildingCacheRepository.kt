package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import org.springframework.scheduling.annotation.Async

interface BuildingCacheRepository : BuildingRepository {
  fun save(building: Building)
}

class RedissonBuildingCacheRepository : BuildingCacheRepository {

  @Async
  override fun save(building: Building) {
    // Save building to Redisson
  }

  override fun findById(id: Long): Building? {
    TODO("Not yet implemented")
  }
}

class NoOpBuildingCacheRepository : BuildingCacheRepository {
  override fun save(building: Building) {
    // Do nothing
  }

  override fun findById(id: Long): Building? {
    return null
  }
}