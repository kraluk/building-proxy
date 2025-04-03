package io.kraluk.buildingproxy.test.shared

import io.micrometer.core.instrument.MeterRegistry
import java.util.concurrent.TimeUnit

fun MeterRegistry.getTimerValue(name: String): Double =
  get(name)
    .timer()
    .totalTime(TimeUnit.MILLISECONDS)