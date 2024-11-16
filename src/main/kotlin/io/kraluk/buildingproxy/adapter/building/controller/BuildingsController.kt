package io.kraluk.buildingproxy.adapter.building.controller

import io.kraluk.buildingproxy.domain.building.entity.BuildingException
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
import org.springframework.web.bind.annotation.ExceptionHandler
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

  @ExceptionHandler(BuildingException::class)
  fun handleException(exception: BuildingException): ResponseEntity<ProblemDetail> =
    internalSystemError(exception)
}

private fun notFoundOf(id: Long): ResponseEntity<ProblemDetail> =
  problemDetail(
    status = HttpStatus.NOT_FOUND,
    detail = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      "Building with id '$id' does not exist!",
    ),
  )

private fun internalSystemError(e: Exception): ResponseEntity<ProblemDetail> =
  problemDetail(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    detail = ProblemDetail.forStatusAndDetail(
      HttpStatus.INTERNAL_SERVER_ERROR,
      // maybe it'd better to avoid showing the exception message to the client, but for the sake of the example, it's ok
      "Cannot process query to the upstream service due to the following internal system error - '${e.message}'",
    ),
  )

private fun problemDetail(status: HttpStatus, detail: ProblemDetail): ResponseEntity<ProblemDetail> =
  ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(detail)