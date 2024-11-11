package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import io.kraluk.buildingproxy.shared.logger
import io.micrometer.core.annotation.Timed
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

interface BuildingCacheRepository : BuildingRepository {
  fun save(building: Building)
}

@ConditionalOnMissingBean(RedissonBuildingCacheRepository::class)
@Component
class NoOpBuildingCacheRepository : BuildingCacheRepository {
  override fun save(building: Building) {
    // Do nothing
  }

  override fun findById(id: Long): Building? {
    return null
  }
}

@ConditionalOnExpression("#{\${app.building.cache.enabled:false} and \${spring.data.redis.enabled:false}}")
@Component
class RedissonBuildingCacheRepository(
  private val client: RedissonClient,
  private val clock: Clock,
  private val properties: BuildingCacheProperties,
) : BuildingCacheRepository {

  @Async
  override fun save(building: Building) {
    client.getBucket<Building>(indexKey(building.id))
      .apply {
        set(building)
        expire(keyExpireTimestamp())
      }
      .also { log.debug("Saved '1' element under the key '{}'", indexKey(building.id)) }
  }

  @Timed("building_fetch_cache")
  override fun findById(id: Long): Building? =
    client.getBucket<Building>(indexKey(id))
      .let { if (it.size() > 1L) it.get() else null }

  private fun keyExpireTimestamp(): Instant =
    clock.instant() + properties.timeToLive

  companion object {
    private val log by logger()

    private fun indexKey(id: Long): String =
      "building_id_$id"
  }
}

@ConfigurationProperties(prefix = "app.building.cache")
data class BuildingCacheProperties(val timeToLive: Duration = 10.minutes.toJavaDuration())