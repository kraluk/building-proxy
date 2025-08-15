package io.kraluk.buildingproxy.configuration

import io.kraluk.buildingproxy.shared.logger
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import java.lang.reflect.Method
import java.util.concurrent.Executor

@Configuration
@EnableAsync(proxyTargetClass = true)
class AsyncConfiguration {

  @Bean
  fun asyncTaskExecutor(builder: SimpleAsyncTaskExecutorBuilder): SimpleAsyncTaskExecutor =
    builder
      .build()

  @Bean
  fun asyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler =
    LoggingAsyncUncaughtExceptionHandler()
}

/**
 * Support configuration for Spring [org.springframework.scheduling.annotation.Async] methods via [AsyncSupportConfiguration]
 * - it's necessary to provide custom exception handler and custom executor.
 */
@Configuration
class AsyncSupportConfiguration(
  @param:Qualifier("asyncTaskExecutor") private val asyncTaskExecutor: SimpleAsyncTaskExecutor,
  private val exceptionHandler: AsyncUncaughtExceptionHandler,
) : AsyncConfigurer {

  override fun getAsyncExecutor(): Executor =
    asyncTaskExecutor

  override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler =
    exceptionHandler
}

class LoggingAsyncUncaughtExceptionHandler : AsyncUncaughtExceptionHandler {

  override fun handleUncaughtException(throwable: Throwable, method: Method, vararg params: Any) {
    log.error("Uncaught exception in async method '{}' with parameters '{}'", method, params, throwable)
  }

  companion object {
    private val log by logger()
  }
}