package io.kraluk.buildingproxy.test.shared

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import java.util.concurrent.TimeUnit

fun MeterRegistry.getTimerValue(name: String): Double =
  meters
    .filterIsInstance<Timer>()
    .first { m -> m.id.name == name }
    .totalTime(TimeUnit.MILLISECONDS)