package io.kraluk.buildingproxy.domain.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building

interface BuildingRepository {

  fun findById(id: Long): Building?
}