package ch.chrigu.demo.domainvalidation

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @PostMapping("/")
    fun testCustomResolver(@ValidatedDomain customer: Customer) = customer

    @PostMapping("/standard")
    fun testStandardBehavior(@RequestBody customer: Customer) = customer
}
