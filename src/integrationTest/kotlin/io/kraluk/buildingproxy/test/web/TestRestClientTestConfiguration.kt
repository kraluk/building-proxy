package io.kraluk.buildingproxy.test.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestClient
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.net.http.HttpClient

@TestConfiguration
class TestRestClientTestConfiguration {

  @Bean("testRestClient")
  fun testRestClient(environment: Environment, mapper: ObjectMapper): RestClient {
    val uriBuilderFactory = LocalHostUriBuilderFactory(environment)

    val httpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1) // As wiremock has issues with HTTP 2, we need to force HTTP 1.1
      .followRedirects(HttpClient.Redirect.NEVER)
      .build()

    return RestClient.builder()
      .messageConverters { converters ->
        converters
          .filterIsInstance<MappingJackson2HttpMessageConverter>()
          .forEach { converter -> converter.objectMapper = mapper }
      }
      .uriBuilderFactory(uriBuilderFactory)
      .defaultStatusHandler(NoOpErrorStatusHandler())
      .requestFactory(JdkClientHttpRequestFactory(httpClient))
      .build()
  }
}

internal class LocalHostUriBuilderFactory(environment: Environment) : DefaultUriBuilderFactory() {
  private val delegate = LocalHostUriTemplateHandler(environment)

  override fun expand(uriTemplate: String, vararg uriVariables: Any): URI {
    return delegate.expand(uriTemplate, *uriVariables)
  }

  override fun uriString(uriTemplate: String): UriBuilder {
    return DefaultUriBuilderFactory(delegate.rootUri).uriString(uriTemplate)
  }

  override fun builder(): UriBuilder {
    return DefaultUriBuilderFactory(delegate.rootUri).uriString("")
  }
}

internal class NoOpErrorStatusHandler : DefaultResponseErrorHandler() {
  override fun handleError(response: ClientHttpResponse) {
    // noop as we want to make assertions on non-200 responses in tests
  }
}