package io.kraluk.buildingproxy.test.web

import org.springframework.boot.restclient.RootUriTemplateHandler
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestClient
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriTemplateHandler
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.net.http.HttpClient

@TestConfiguration
class TestRestClientTestConfiguration {

  @Bean("testRestClient")
  fun testRestClient(environment: Environment, mapper: JsonMapper): RestClient {
    val uriBuilderFactory = LocalHostUriBuilderFactory(environment)

    val httpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1) // As wiremock has issues with HTTP 2, we need to force HTTP 1.1
      .followRedirects(HttpClient.Redirect.NEVER)
      .build()

    return RestClient.builder()
      // .configureMessageConverters { c -> c.withJsonConverter(JacksonJsonHttpMessageConverter(mapper)) }
      .uriBuilderFactory(uriBuilderFactory)
      .defaultStatusHandler(NoOpErrorStatusHandler())
      .requestFactory(JdkClientHttpRequestFactory(httpClient))
      .build()
  }
}

internal class LocalHostUriBuilderFactory(environment: Environment) : DefaultUriBuilderFactory() {
  private val delegate = LocalHostUriTemplateHandler(environment)

  override fun expand(uriTemplate: String, vararg uriVariables: Any?): URI =
    delegate.expand(uriTemplate, *uriVariables)

  override fun uriString(uriTemplate: String): UriBuilder =
    DefaultUriBuilderFactory(delegate.rootUri).uriString(uriTemplate)

  override fun builder(): UriBuilder =
    DefaultUriBuilderFactory(delegate.rootUri).uriString("")
}

internal class NoOpErrorStatusHandler : DefaultResponseErrorHandler() {
  override fun handleError(
    response: ClientHttpResponse,
    statusCode: HttpStatusCode,
    url: URI?,
    method: HttpMethod?,
  ) {
    // noop as we want to make assertions on non-200 responses in tests
  }
}

class LocalHostUriTemplateHandler(
  private val environment: Environment,
  private val scheme: String = "http",
  handler: UriTemplateHandler = DefaultUriBuilderFactory(),
) : RootUriTemplateHandler(handler) {

  override fun getRootUri(): String {
    val port = this.environment.getProperty("local.server.port", "8080")
    val contextPath = this.environment.getProperty(PREFIX + "context-path", "")
    return this.scheme + "://localhost:" + port + contextPath
  }

  companion object {
    private const val PREFIX = "server.servlet."
  }
}