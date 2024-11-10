package io.kraluk.buildingproxy.usecase.building

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import io.kraluk.buildingproxy.shared.logger
import org.springframework.stereotype.Component

@Component
class FindBuildingByIdUseCase(
  private val repository: BuildingRepository,
) {
  fun invoke(command: Command): Building? =
    repository.findById(command.id)
      ?.also { log.info("Building with id '{}' has been found", command.id) }

  companion object {
    private val log by logger()
  }

  data class Command(val id: Long)
}