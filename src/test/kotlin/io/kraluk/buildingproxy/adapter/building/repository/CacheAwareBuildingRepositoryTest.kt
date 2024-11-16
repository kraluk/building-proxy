package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.test.adapter.building.repository.InMemoryBuildingCacheRepository
import io.kraluk.buildingproxy.test.adapter.building.repository.InMemoryBuildingCacheRepository.Companion.THROWING_FIND_ID
import io.kraluk.buildingproxy.test.adapter.building.repository.InMemoryBuildingCacheRepository.Companion.THROWING_SAVE_ID
import io.kraluk.buildingproxy.test.adapter.building.repository.InMemoryBuildingRepository
import io.kraluk.buildingproxy.test.entity.building
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be null`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

class CacheAwareBuildingRepositoryTest {

  private val regularRepository = spy(InMemoryBuildingRepository())
  private val cacheRepository = InMemoryBuildingCacheRepository()

  private val cacheAwareRepository = CacheAwareBuildingRepository(
    repository = regularRepository,
    cacheRepository = cacheRepository,
  )

  @Test
  fun `should find element in cache and not call regular repository`() {
    // Given building is already in cache
    val id = 111L
    val cached = building(id = id)
    cacheRepository.save(cached)

    // When
    val result = cacheAwareRepository.findById(id)

    // Then
    result.`should not be null`().id `should be equal to` id

    // And then verify that regular repository was not called
    verify(regularRepository, never())
      .findById(any())
  }

  @Test
  fun `should find element in regular repository as it was not cached and then cache it`() {
    // Given building is already in cache
    val id = 111L
    val notCached = building(id = id)
    regularRepository.save(notCached)

    // When
    val result = cacheAwareRepository.findById(id)

    // Then
    result.`should not be null`().id `should be equal to` id

    // And then verify that regular repository was not called
    cacheRepository.elements()
      .shouldHaveSize(1)
      .first()
      .id `should be equal to` id
  }

  @Test
  fun `should find element in regular repository as there were some issues when trying to get element from cache`() {
    // Given building is already in cache
    val id = THROWING_FIND_ID
    val element = building(id = id)
    regularRepository.save(element)

    // When
    val result = cacheAwareRepository.findById(id)

    // Then
    result.`should not be null`().id `should be equal to` id
  }

  @Test
  fun `should find element in regular repository as it was not cached and it was not possible to cache it`() {
    // Given building is already in cache
    val id = THROWING_SAVE_ID
    val notCached = building(id = id)
    regularRepository.save(notCached)

    // When
    val result = cacheAwareRepository.findById(id)

    // Then
    result.`should not be null`().id `should be equal to` id
  }
}