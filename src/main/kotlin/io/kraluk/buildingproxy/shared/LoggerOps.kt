package io.kraluk.buildingproxy.shared

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

fun <R : Any> logger(): ReadOnlyProperty<R, Logger> = LoggerDelegate()

private class LoggerDelegate<R : Any> : ReadOnlyProperty<R, Logger> {
  override fun getValue(thisRef: R, property: KProperty<*>): Logger =
    LoggerFactory.getLogger(thisRef.javaClass.getClassForLogging())
}

private fun Class<*>.getClassForLogging(): Class<*> =
  if (isCompanionObject) {
    enclosingClass
  } else {
    this
  }

private val Class<*>.isCompanionObject: Boolean
  get() = this == this.enclosingClass?.kotlin?.companionObject?.java