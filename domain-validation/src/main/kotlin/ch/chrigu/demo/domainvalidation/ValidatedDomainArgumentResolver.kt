package ch.chrigu.demo.domainvalidation

import com.fasterxml.jackson.databind.ObjectMapper
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
            .onErrorMap { it.asInvalidDomainException() }
    }
}
