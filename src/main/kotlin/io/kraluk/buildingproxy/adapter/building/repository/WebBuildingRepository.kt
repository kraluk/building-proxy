package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import org.springframework.stereotype.Component

@Component
class WebBuildingRepository : BuildingRepository {
  override fun findById(id: String): Building? {
    TODO("Not yet implemented")
  }
}