package io.kraluk.buildingproxy.adapter.building.web

import io.kraluk.buildingproxy.test.IntegrationTest
import io.kraluk.buildingproxy.test.contentOf
import io.kraluk.buildingproxy.test.web.expectContentType
import io.kraluk.buildingproxy.test.web.expectStatusCode
import io.kraluk.buildingproxy.test.web.expectStatusCodeOk
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class BuildingsControllerIntegrationTest : IntegrationTest() {

  private val buildingsClient by lazy { BuildingsWebTestClient(testRestClient) }

  @Test
  fun `should find building by its id`() {
    // Given
    val id = 1L

    // When
    val actual = buildingsClient.getById<String>(id)
      // Then
      .expectStatusCodeOk()
      .expectContentType(MediaType.APPLICATION_JSON_VALUE)
      .body
      .`should not be null`()

    // Then check the body
    val expected = contentOf("building/web/get-building-1.json")
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
  }

  @Test
  fun `should not find building by id when received 404 from the upstream API`() {
    // Given
    val id = 666L

    // When
    val actual = buildingsClient.getById<String>(id)
      // Then
      .expectStatusCode(HttpStatus.NOT_FOUND)
      .expectContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
      .body
      .`should not be null`()

    // Then check the body
    val expected = contentOf("building/web/get-building-666-not-found.json")
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
  }
}