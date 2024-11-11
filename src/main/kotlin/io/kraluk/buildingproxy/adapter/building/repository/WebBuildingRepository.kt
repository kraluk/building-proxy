package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.domain.building.entity.Building
import io.kraluk.buildingproxy.domain.building.entity.BuildingException
import io.kraluk.buildingproxy.domain.building.entity.BuildingHasTooManyFloorsException
import io.kraluk.buildingproxy.domain.building.repository.BuildingRepository
import io.kraluk.buildingproxy.shared.logger
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException

@Component
class WebBuildingRepository(
  private val client: KontaktBuildingClient,
) : BuildingRepository {
  override fun findById(id: Long): Building? =
    try {
      queryBuildingById(id)
    } catch (e: HttpClientErrorException) { // perhaps this should be handled in a more not-so-generic way
      if (e.statusCode.isNotFound()) {
        log.warn("Building with id '{}' not found", id)
        null
      } else {
        throw BuildingException("Upstream client error while fetching building with id '$id'", buildingId = id, cause = e)
      }
    } catch (e: HttpServerErrorException) {
      log.error("Server error while fetching building with id '$id' - '{}'", e.responseBodyAsString, e)
      throw BuildingException("Upstream server error while fetching building with id '$id'", buildingId = id, cause = e)
    }

  private fun queryBuildingById(id: Long): Building? =
    client.findById(id)
      .also {
        // maybe it should not be responsibility of the repository to handle this kind of logic...
        if (it.page.totalElements != 1) {
          throw BuildingHasTooManyFloorsException("There is more than one building per single id '$id'! - '$it'")
        }
      }
      .content
      .firstOrNull()
      ?.toDomain()
      ?: null.also { log.warn("Building with id '{}' has not content!", id) }

  companion object {
    private val log by logger()

    private fun HttpStatusCode.isNotFound() =
      this == HttpStatusCode.valueOf(404)
  }
}