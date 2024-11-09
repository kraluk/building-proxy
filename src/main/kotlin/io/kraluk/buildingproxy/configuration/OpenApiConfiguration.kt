package io.kraluk.buildingproxy.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

  @Bean
  fun openApi(): OpenAPI =
    OpenAPI()
      .info(
        Info()
          .title("Building Proxy API")
          .description("Proxies and caches calls to Building API")
          .version("v1"),
      )
}