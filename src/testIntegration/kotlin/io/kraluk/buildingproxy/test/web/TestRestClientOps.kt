package io.kraluk.buildingproxy.test.web

import org.amshove.kluent.`should be equal to`
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity

internal inline fun <reified T : Any> RestClient.RequestHeadersSpec<*>.retrieveAsEntity(): ResponseEntity<T> =
  retrieve()
    .onErrorProceed()
    .toEntity<T>()

internal inline fun <reified T> ResponseEntity<T>.expectStatusCode(status: HttpStatus): ResponseEntity<T> {
  statusCode.value() `should be equal to` status.value()
  return this
}

internal inline fun <reified T> ResponseEntity<T>.expectStatusCodeOk() =
  expectStatusCode(HttpStatus.OK)

internal inline fun <reified T> ResponseEntity<T>.expectContentType(value: String): ResponseEntity<T> =
  expectHeader(HttpHeaders.CONTENT_TYPE, value)

internal inline fun <reified T> ResponseEntity<T>.expectHeader(header: String, value: String): ResponseEntity<T> {
  headers.getOrEmpty(header).first() `should be equal to` value
  return this
}

private fun RestClient.ResponseSpec.onErrorProceed(): RestClient.ResponseSpec =
  onStatus({ it.isError }, { _, _ -> }) // avoid throwing exceptions on error status codes