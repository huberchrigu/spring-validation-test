package ch.chrigu.demo.domainvalidation

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // For custom resolver
    @ExceptionHandler
    fun handleInvalidDomain(e: InvalidDomainException) = e.asProblemDetail(HttpStatus.BAD_REQUEST, "Invalid domain object")

    // for standard behavior
    @ExceptionHandler
    fun handleWebInputException(e: Exception) = if (e.cause?.cause == null)
        handleGenericException(e)
    else
        handleGenericException(e.cause!!.cause!!)

    @ExceptionHandler
    fun handleGenericException(e: Throwable) = e.asInvalidDomainException().let {
        if (it is InvalidDomainException)
            handleInvalidDomain(it)
        else
            it.asProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
    }

    private fun Throwable.asProblemDetail(status: HttpStatus, title: String) = ProblemDetail.forStatus(status)
        .also {
            it.detail = message
            it.title = title
        }
}
