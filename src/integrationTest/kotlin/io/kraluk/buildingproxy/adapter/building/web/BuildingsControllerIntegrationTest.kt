package io.kraluk.buildingproxy.adapter.building.web

import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
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
  fun `should not find building by id when received 200 from the upstream API but without any valid content`() {
    // Given
    val id = 2L

    // When
    val actual = buildingsClient.getById<String>(id)
      // Then
      .expectStatusCode(HttpStatus.NOT_FOUND)
      .expectContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
      .body
      .`should not be null`()

    // Then check the body
    val expected = contentOf("building/web/get-building-2-not-found.json")
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

  @Test
  fun `should return error message when received server error from the upstream API after configured amount of retries`() {
    // Given
    val id = 999L

    // When
    val actual = buildingsClient.getById<String>(id)
      // Then
      .expectStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
      .expectContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
      .body
      .`should not be null`()

    // Then check the body
    val expected = contentOf("building/web/get-building-999-upstream-error.json")
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)

    // And then check the number of retries
    wireMock.verify(
      exactly(3),
      getRequestedFor(urlEqualTo("/v2/locations/buildings?buildingId=$id&size=1")),
    )
  }
}