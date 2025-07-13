package ch.chrigu.demo.domainvalidation

class Customer(val name: String, val age: Int?) {
    init {
        require(name.isNotBlank())
        require(age == null || age >= 18) {"Age must be 18 or greater"}
    }
}
