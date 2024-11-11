package io.kraluk.buildingproxy.adapter.building.repository

import io.kraluk.buildingproxy.adapter.building.repository.InMemoryKontaktClient.Companion.EMPTY_LIST_ID
import io.kraluk.buildingproxy.adapter.building.repository.InMemoryKontaktClient.Companion.FINDABLE_ID
import io.kraluk.buildingproxy.adapter.building.repository.InMemoryKontaktClient.Companion.MORE_THAN_ONE_BUILDING_PER_ID
import io.kraluk.buildingproxy.adapter.building.repository.InMemoryKontaktClient.Companion.NOT_FOUND_ID
import io.kraluk.buildingproxy.domain.building.entity.BuildingException
import io.kraluk.buildingproxy.domain.building.entity.BuildingHasTooManyFloorsException
import io.kraluk.buildingproxy.test.adapter.building.repository.kontaktBuilding
import io.kraluk.buildingproxy.test.adapter.building.repository.kontaktBuildings
import io.kraluk.buildingproxy.test.adapter.building.repository.kontaktPage
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should not be null`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.nio.charset.StandardCharsets.UTF_8

class WebBuildingRepositoryTest {

  private val client = InMemoryKontaktClient()
  private val repository = WebBuildingRepository(client)

  @Test
  fun `should find successfully building by its id`() {
    // Given
    val id = FINDABLE_ID

    // When
    val result = repository.findById(id)

    // Then
    val building = result.`should not be null`()
    building.id `should be equal to` id
    building.name `should be equal to` "Pałac w Radzionkowie"
  }

  @Test
  fun `should not find any building by id when not found code is returned`() {
    // Given
    val id = NOT_FOUND_ID

    // When
    val result = repository.findById(id)

    // Then
    result.`should be null`()
  }

  @Test
  fun `should not find any building by id when ok code is returned but building list is empty`() {
    // Given
    val id = EMPTY_LIST_ID

    // When
    val result = repository.findById(id)

    // Then
    result.`should be null`()
  }

  @Test
  fun `should throw exception when more than one building is returned for single id`() {
    // Given
    val id = MORE_THAN_ONE_BUILDING_PER_ID

    // When & Then
    val e = invoking {
      repository.findById(id)
    } `should throw` BuildingHasTooManyFloorsException::class

    // And then
    e.exceptionMessage `should contain` "There is more than one building per single id '$id'!"
  }

  @Test
  fun `should throw exception when client is not responding successfully for the request`() {
    // Given
    val id = Long.MAX_VALUE

    // When & Then
    val e = invoking {
      repository.findById(id)
    } `should throw` BuildingException::class

    // And then
    e.exceptionMessage `should contain` "Upstream server error while fetching building with id '$id'"
  }
}

private class InMemoryKontaktClient : KontaktClient {

  override fun findById(id: Long): KontaktBuildings =
    when (id) {
      FINDABLE_ID -> kontaktBuildings(
        content = listOf(kontaktBuilding(id = 1L, name = "Pałac w Radzionkowie")),
      )

      EMPTY_LIST_ID -> kontaktBuildings(
        content = listOf(),
      )

      NOT_FOUND_ID -> {
        throw HttpClientErrorException.create(
          HttpStatusCode.valueOf(404),
          "Not Found",
          HttpHeaders(),
          "Not Found".toByteArray(),
          UTF_8,
        )
      }

      MORE_THAN_ONE_BUILDING_PER_ID -> kontaktBuildings(
        content = listOf(kontaktBuilding(), kontaktBuilding()),
        page = kontaktPage(totalElements = 2),
      )

      else -> {
        throw HttpServerErrorException.create(
          HttpStatusCode.valueOf(500),
          "Internal System Error",
          HttpHeaders(),
          "Internal System Error".toByteArray(),
          UTF_8,
        )
      }
    }

  companion object {
    const val FINDABLE_ID = 1L
    const val EMPTY_LIST_ID = 40L
    const val NOT_FOUND_ID = 50L
    const val MORE_THAN_ONE_BUILDING_PER_ID = 666L
  }
}