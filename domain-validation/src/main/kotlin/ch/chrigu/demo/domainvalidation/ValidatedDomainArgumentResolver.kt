package ch.chrigu.demo.domainvalidation

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.core.MethodParameter
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.Charset

class ValidatedDomainArgumentResolver(private val objectMapper: ObjectMapper) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(ValidatedDomain::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return DataBufferUtils.join(exchange.request.body)
            .map { objectMapper.readValue(it.toString(Charset.defaultCharset()), parameter.parameterType) }
            .onErrorMap { toInvalidDomainException(it) }
    }

    private fun toInvalidDomainException(t: Throwable) = when (t) {
        is MissingKotlinParameterException -> InvalidDomainException("The required property ${t.parameter.name} is missing", t)
        is ValueInstantiationException if t.cause is IllegalArgumentException -> InvalidDomainException("Invalid domain object: " + t.cause?.message, t)
        is JsonParseException -> InvalidDomainException("Invalid JSON: " + t.originalMessage, t)
        else -> t
    }
}

class InvalidDomainException(message: String?, cause: Throwable) : RuntimeException(message, cause)
