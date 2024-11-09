package io.kraluk.buildingproxy.test

import io.kraluk.buildingproxy.test.web.TestRestClientTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient

@ActiveProfiles("integration")
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@AutoConfigureWireMock(port = 0)
@Import(TestRestClientTestConfiguration::class)
abstract class IntegrationTest {

  @Qualifier("testRestClient")
  @Autowired
  protected lateinit var testRestClient: RestClient
}