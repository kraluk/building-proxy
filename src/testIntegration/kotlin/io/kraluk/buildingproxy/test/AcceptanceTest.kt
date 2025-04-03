package io.kraluk.buildingproxy.test

import eu.rekawek.toxiproxy.Proxy
import eu.rekawek.toxiproxy.ToxiproxyClient
import eu.rekawek.toxiproxy.model.ToxicDirection
import io.kraluk.buildingproxy.test.extension.ClearCacheExtension
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.ToxiproxyContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * E2E test class for the Building Proxy application.
 */
@Testcontainers
@ExtendWith(ClearCacheExtension::class)
abstract class AcceptanceTest : IntegrationTest() {

  @Autowired
  protected lateinit var meterRegistry: MeterRegistry

  protected fun startRedisMalfunction() {
    redisProxy.toxics().bandwidth("CUT_CONNECTION_DOWNSTREAM", ToxicDirection.DOWNSTREAM, 0)
    redisProxy.toxics().bandwidth("CUT_CONNECTION_UPSTREAM", ToxicDirection.UPSTREAM, 0)
  }

  protected fun stopRedisMalfunction() {
    redisProxy.toxics().get("CUT_CONNECTION_DOWNSTREAM").remove()
    redisProxy.toxics().get("CUT_CONNECTION_UPSTREAM").remove()
  }

  companion object {
    private const val REDIS_PROXY_PORT = 8666
    private const val REDIS_PORT = 6379

    private lateinit var redisProxy: Proxy

    @JvmStatic
    private fun initProxyClient(): Proxy {
      val client = ToxiproxyClient(toxiProxyContainer.host, toxiProxyContainer.controlPort)
      return client.createProxy("redis", "0.0.0.0:$REDIS_PROXY_PORT", "redis:$REDIS_PORT")
    }

    @JvmStatic
    @Container
    private val redisContainer: GenericContainer<*> =
      GenericContainer("redis:7.4-alpine")
        .withExposedPorts(REDIS_PORT)
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("redis")
        .withReuse(true)
        .also { it.setWaitStrategy(Wait.forListeningPort()) }

    @JvmStatic
    @Container
    private val toxiProxyContainer: ToxiproxyContainer =
      ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.11.0")
        .withNetwork(redisContainer.network)
        .withReuse(true)

    @Suppress("unused")
    @JvmStatic
    @DynamicPropertySource
    fun redisProperties(registry: DynamicPropertyRegistry) {
      redisProxy = initProxyClient()

      registry.add("spring.data.redis.enabled") { true }
      registry.add("app.building.cache.enabled") { true }
      registry.add("spring.data.redis.host") { toxiProxyContainer.host }
      registry.add("spring.data.redis.port") { toxiProxyContainer.getMappedPort(REDIS_PROXY_PORT) }
    }
  }
}