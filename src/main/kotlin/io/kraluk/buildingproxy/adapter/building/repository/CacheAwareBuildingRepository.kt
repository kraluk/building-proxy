package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import io.kraluk.buildingproxy.shared.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class CacheAwareBuildingRepository(
  @param:Qualifier("webBuildingRepository") private val repository: BuildingRepository,
  private val cacheRepository: BuildingCacheRepository,
) : BuildingRepository {

  override fun findById(id: Long): Building? =
    findInCache(id)
      ?: findAndSaveInCache(id)

  private fun findInCache(id: Long): Building? =
    try {
      cacheRepository.findById(id)
        .also { if (it != null) log.debug("Building '{}' found in cache!", id) }
    } catch (e: Exception) {
      log.error("Unable to get Building '{}' from cache!", id, e)
      null
    }

  private fun findAndSaveInCache(id: Long): Building? =
    repository.findById(id)
      ?.also {
        try {
          cacheRepository.save(it)
            .also { log.debug("Building '{}' saved in cache!", id) }
        } catch (e: Exception) {
          log.error("Unable to save Building '{}' in cache!", id, e)
        }
      }

  companion object {
    private val log by logger()
  }
}