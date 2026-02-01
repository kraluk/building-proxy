package io.kraluk.buildingproxy.test

import com.github.tomakehurst.wiremock.WireMockServer
import io.kraluk.buildingproxy.test.web.TestRestClientTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock

@ActiveProfiles("integration")
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@EnableWireMock(
  value = [
    ConfigureWireMock(
      registerSpringBean = true,
      filesUnderClasspath = "wiremock/",
    ),
  ],
)
@Import(TestRestClientTestConfiguration::class)
abstract class IntegrationTest {

  @Qualifier("testRestClient")
  @Autowired
  protected lateinit var testRestClient: RestClient

  @InjectWireMock
  protected lateinit var wireMock: WireMockServer
}