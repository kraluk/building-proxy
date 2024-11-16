package io.kraluk.buildingproxy.usecase.building

import io.kraluk.buildingproxy.test.adapter.building.repository.InMemoryBuildingRepository
import io.kraluk.buildingproxy.test.entity.building
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

class FindBuildingByIdUseCaseTest {

  private val repository = InMemoryBuildingRepository()
  private val useCase = FindBuildingByIdUseCase(repository = repository)

  @Test
  fun `should find building by id when it's present in the repository`() {
    // Given building is saved
    val id = 1L
    val saved = building(id = id)
    repository.save(saved)

    // When
    val result = useCase.invoke(FindBuildingByIdUseCase.Command(id))

    // Then
    val found = result.`should not be null`()
    found.id `should be equal to` id
    found.name `should be equal to` saved.name
  }

  @Test
  fun `should return null when building is not findable by id`() {
    // Given
    // repository is empty

    // When
    val result = useCase.invoke(FindBuildingByIdUseCase.Command(1L))

    // Then
    result.`should be null`()
  }
}