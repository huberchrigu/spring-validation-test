package ch.chrigu.demo.domainvalidation

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
class TestController {
    @PostMapping("/")
    fun test(@ValidatedDomain customer: Customer) = customer
}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handleInvalidDomain(e: InvalidDomainException) = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        .also {
            it.detail = e.message
            it.title = "Invalid domain object"
        }
}
