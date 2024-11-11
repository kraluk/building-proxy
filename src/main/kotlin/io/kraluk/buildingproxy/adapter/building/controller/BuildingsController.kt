package io.kraluk.buildingproxy.adapter.building.controller

import io.kraluk.buildingproxy.shared.contract.http.BuildingHttp
import io.kraluk.buildingproxy.shared.contract.http.toHttp
import io.kraluk.buildingproxy.usecase.building.FindBuildingByIdUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Buldings Proxy API")
@RestController
class BuildingsController(
  private val findByIsUseCase: FindBuildingByIdUseCase,
) {

  @Operation(
    summary = "Find Building by Id via proxing to the Building API",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Found a building",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = BuildingHttp::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Building not found",
        content = [
          Content(
            mediaType = APPLICATION_PROBLEM_JSON_VALUE,
            schema = Schema(implementation = ProblemDetail::class),
          ),
        ],
      ),
    ],
  )
  @GetMapping(value = ["/v1/buildings/{id}"], produces = [APPLICATION_JSON_VALUE])
  fun findById(@Parameter(description = "Id of the Building", example = "34567") @PathVariable("id") id: Long): ResponseEntity<out Any> =
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