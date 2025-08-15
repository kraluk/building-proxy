package io.kraluk.buildingproxy.adapter.building.web

import io.kraluk.buildingproxy.test.web.retrieveAsEntity
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriBuilder

internal class BuildingsWebTestClient(private val client: RestClient) {

  internal inline fun <reified R : Any> getById(id: Long): ResponseEntity<R> =
    client
      .get()
      .uri { builder: UriBuilder ->
        builder
          .path("/v1/buildings")
          .pathSegment("{id}")
          .build(id)
      }
      .accept(MediaType.APPLICATION_JSON)
      .retrieveAsEntity<R>()
}