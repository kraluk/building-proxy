package io.kraluk.buildingproxy.adapter.building.controller

import io.kraluk.buildingproxy.shared.contract.http.toHttp
import io.kraluk.buildingproxy.usecase.building.FindBuildingByIdUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BuildingsController(
  private val findByIsUseCase: FindBuildingByIdUseCase,
) {

  @GetMapping(value = ["/v1/buildings/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  fun findById(@PathVariable("id") id: Long): ResponseEntity<out Any> =
    findByIsUseCase.invoke(FindBuildingByIdUseCase.Command(id))
      ?.toHttp()
      ?.let { ResponseEntity.ok(it) }
      ?: notFoundOf(id)
}

private fun notFoundOf(id: Long): ResponseEntity<ProblemDetail> =
  notFound(
    ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      "Building with id '$id' does not exist!",
    ),
  )

private fun notFound(detail: ProblemDetail): ResponseEntity<ProblemDetail> =
  ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(detail)