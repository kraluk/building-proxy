package io.kraluk.buildingproxy.test.extension

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.redisson.api.RedissonClient
import org.springframework.test.context.junit.jupiter.SpringExtension

class ClearCacheExtension : AfterEachCallback {

  override fun afterEach(context: ExtensionContext) {
    val redisson = SpringExtension.getApplicationContext(context).getBean(RedissonClient::class.java)

    redisson.keys.flushall() // https://github.com/redisson/redisson/issues/3185
  }
}