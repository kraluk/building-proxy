package io.kraluk.buildingproxy.shared.web

import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import java.io.IOException
import java.net.http.HttpClient
import java.net.http.HttpClient.Version.HTTP_1_1
import java.net.http.HttpClient.Version.HTTP_2
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Duration

object RestClientFactory {

  fun create(
    builder: RestClient.Builder,
    properties: BaseClientProperties,
    http2: Boolean = false,
  ): RestClient {
    // New Java native Http Client uses connection pooling by default. What's worth noticing, by default, it creates as many connections
    // as needed and keeps them alive for 20 minutes.
    // It can be configured by system properties: https://www.baeldung.com/java-httpclient-connection-management
    val httpClient = HttpClient
      .newBuilder()
      .connectTimeout(properties.connectionTimeout)
      .version(if (http2) HTTP_2 else HTTP_1_1) // wiremock has some issues with http2: https://github.com/wiremock/wiremock/issues/2459
      .build()

    val requestFactory = JdkClientHttpRequestFactory(httpClient)
      .also { it.setReadTimeout(properties.readTimeout) }

    val retryingInterceptor = RetryingClientHttpRequestInterceptor(properties.retry)

    val restClient = builder
      .baseUrl(properties.baseUrl)
      .requestFactory(requestFactory)
      .requestInterceptor(retryingInterceptor)
      .build()

    return restClient
  }

  class RetryingClientHttpRequestInterceptor(retryProperties: BaseClientProperties.RetryProperties) : ClientHttpRequestInterceptor {

    private val retry: Retry = Retry.of(
      "interceptor-http-retry",
      retryConfig(
        maxAttempts = retryProperties.maxAttempts,
        interval = ofExponentialBackoff(retryProperties.firstBackoff),
        retryIfResult = { it.statusCode.is5xxServerError },
        retryIfException = {
          // Do not check RestClientExceptions here (like HttpServerErrorException.BadGateway), because the interceptor checks the response
          // code before RestClient maps it to the RestClientException.
          it is IOException
        },
      ),
    )

    private fun retryConfig(
      maxAttempts: Int,
      interval: IntervalFunction,
      retryIfResult: (ClientHttpResponse) -> Boolean,
      retryIfException: (Throwable) -> Boolean,
    ) =
      RetryConfig.custom<ClientHttpResponse>()
        .maxAttempts(maxAttempts)
        .intervalFunction(interval)
        .retryOnResult(retryIfResult)
        .retryOnException(retryIfException)
        .build()

    override fun intercept(
      request: HttpRequest,
      body: ByteArray,
      execution: ClientHttpRequestExecution,
    ): ClientHttpResponse =
      // Executing HTTP request may throw an IOException, which is a checked exception in Java. Normally, you'd see a compilation error,
      // but in Kotlin, all exceptions are unchecked, so no errors are shown here.
      // Do not use `executeSupplier` (unchecked) version here, because it would swallow all checked exceptions, and effectively would not
      // retry them.
      retry.executeCheckedSupplier {
        execution.execute(request, body)
      }
  }

  fun RestClient.RequestHeadersSpec<*>.exchangeIfResponseIsSuccess(): RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse =
    exchange(
      { _, response ->
        when {
          response.statusCode.is2xxSuccessful -> response

          response.statusCode.is4xxClientError ->
            throw HttpClientErrorException.create(
              response.statusCode,
              response.statusText,
              response.headers,
              response.use { it.body.readBytes() },
              UTF_8,
            )

          else ->
            throw HttpServerErrorException.create(
              response.statusCode,
              response.statusText,
              response.headers,
              response.use { it.body.readBytes() },
              UTF_8,
            )
        }
      },
      false,
    )
}

interface BaseClientProperties {
  val baseUrl: String
  val readTimeout: Duration
  val connectionTimeout: Duration
  val retry: RetryProperties

  data class RetryProperties(
    val firstBackoff: Duration,
    val maxAttempts: Int,
  )
}