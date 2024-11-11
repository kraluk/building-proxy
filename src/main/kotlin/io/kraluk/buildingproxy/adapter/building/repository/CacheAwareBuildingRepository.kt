package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import io.kraluk.buildingproxy.shared.logger

class CacheAwareBuildingRepository(
  private val repository: BuildingRepository,
  private val cacheRepository: BuildingCacheRepository,
) : BuildingRepository {

  override fun findById(id: Long): Building? =
    findInCache(id)
      ?: findAndSaveInCache(id)

  private fun findInCache(id: Long): Building? =
    try {
      cacheRepository.findById(id)
    } catch (e: Exception) {
      log.error("Unable to get Building '{}' from cache!", id, e)
      null
    }

  private fun findAndSaveInCache(id: Long): Building? =
    repository.findById(id)
      ?.also {
        try {
          cacheRepository.save(it)
        } catch (e: Exception) {
          log.error("Unable to save Building '{}' in cache!", id, e)
        }
      }

  companion object {
    private val log by logger()
  }
}