package ch.chrigu.demo.domainvalidation

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException

class InvalidDomainException(message: String?, cause: Throwable) : RuntimeException(message, cause)

fun Throwable.asInvalidDomainException() = when (this) {
    is MissingKotlinParameterException -> InvalidDomainException("The required property ${this.parameter.name} is missing", this)
    is ValueInstantiationException if this.cause is IllegalArgumentException -> InvalidDomainException("Invalid domain object: " + this.cause?.message, this)
    is JsonParseException -> InvalidDomainException("Invalid JSON: " + this.originalMessage, this)
    else -> this
}
