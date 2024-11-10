package io.kraluk.buildingproxy.adapter.building.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kraluk.buildingproxy.shared.logger
import io.kraluk.buildingproxy.shared.web.BaseClientProperties
import io.kraluk.buildingproxy.shared.web.RestClientFactory
import io.kraluk.buildingproxy.shared.web.RestClientFactory.exchangeIfResponseIsSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriBuilder
import java.time.Duration

@Component
class RestKontaktBuildingClient(
  @Qualifier("kontaktBuildingClient") private val client: RestClient,
  private val mapper: ObjectMapper,
  private val properties: KontaktBuildingClientProperties,
) : KontaktBuildingClient {
  override fun findById(id: Long): KontaktBuildings =
    client
      .get()
      .uri { it.withInitParams(id).build().also { uri -> log.debug("Calling URL - '{}'", uri) } }
      .header(API_KEY_HEADER, properties.apiKey)
      .accept(MediaType.APPLICATION_JSON)
      .exchangeIfResponseIsSuccess()
      .use { mapper.readValue<KontaktBuildings>(it.body) }
      .also { log.debug("Received the following response - '{}'", it) }

  private fun UriBuilder.withInitParams(id: Long): UriBuilder =
    this
      .path("/v2/locations/buildings")
      .queryParam("buildingId", id)
      .queryParam("size", properties.pageSize)

  companion object {
    private val log by logger()

    private const val API_KEY_HEADER = "Api-Key"
  }
}

@ConfigurationProperties(prefix = "app.building.kontakt-client")
data class KontaktBuildingClientProperties(
  val apiKey: String,
  val pageSize: Int = 1,
  override val baseUrl: String,
  override val readTimeout: Duration,
  override val connectionTimeout: Duration,
  override val retry: BaseClientProperties.RetryProperties,
) : BaseClientProperties

@Configuration
class RestKontaktBuildingClientConfiguration {

  @Bean
  fun kontaktBuildingClient(builder: RestClient.Builder, properties: KontaktBuildingClientProperties) =
    RestClientFactory.create(builder = builder, properties = properties, http2 = false)
}