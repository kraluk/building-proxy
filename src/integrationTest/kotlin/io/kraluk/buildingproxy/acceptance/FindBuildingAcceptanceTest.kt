package io.kraluk.buildingproxy.acceptance

import io.kraluk.buildingproxy.adapter.building.web.BuildingsWebTestClient
import io.kraluk.buildingproxy.test.AcceptanceTest
import io.kraluk.buildingproxy.test.contentOf
import io.kraluk.buildingproxy.test.web.expectContentType
import io.kraluk.buildingproxy.test.web.expectStatusCodeOk
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.http.MediaType

class FindBuildingAcceptanceTest : AcceptanceTest() {

  private val buildingsClient by lazy { BuildingsWebTestClient(testRestClient) }

  @Test
  fun `should find building by its id and should cache it after second call`() {
    // Given
    val id = 1L

    // When
    val actual1 = buildingsClient.getById<String>(id)
      .expectStatusCodeOk()
      .expectContentType(MediaType.APPLICATION_JSON_VALUE)
      .body
      .`should not be null`()

    val actual2 = buildingsClient.getById<String>(id)
      .expectStatusCodeOk()
      .expectContentType(MediaType.APPLICATION_JSON_VALUE)
      .body
      .`should not be null`()

    // Then check the body
    val expected = contentOf("building/web/get-building-1.json")

    JSONAssert.assertEquals(expected, actual1, JSONCompareMode.STRICT)
    JSONAssert.assertEquals(expected, actual2, JSONCompareMode.STRICT)
  }

  @Test
  fun `should find building by its id despite of cache connectivity issues`() {
    // Given
    val id = 1L

    // When
    val actual1 = buildingsClient.getById<String>(id)
      .expectStatusCodeOk()
      .expectContentType(MediaType.APPLICATION_JSON_VALUE)
      .body
      .`should not be null`()

    // And then there are issues with cache connectivity
    startRedisMalfunction()

    val actual2 = buildingsClient.getById<String>(id)
      .expectStatusCodeOk()
      .expectContentType(MediaType.APPLICATION_JSON_VALUE)
      .body
      .`should not be null`()

    // And then release cache malfunction
    stopRedisMalfunction()

    val actual3 = buildingsClient.getById<String>(id)
      .expectStatusCodeOk()
      .expectContentType(MediaType.APPLICATION_JSON_VALUE)
      .body
      .`should not be null`()

    // Then check the body
    val expected = contentOf("building/web/get-building-1.json")

    JSONAssert.assertEquals(expected, actual1, JSONCompareMode.STRICT)
    JSONAssert.assertEquals(expected, actual2, JSONCompareMode.STRICT)
    JSONAssert.assertEquals(expected, actual3, JSONCompareMode.STRICT)
  }
}