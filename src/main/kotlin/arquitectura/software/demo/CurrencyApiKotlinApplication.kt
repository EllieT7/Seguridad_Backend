package arquitectura.software.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CurrencyApiKotlinApplication

fun main(args: Array<String>) {
	runApplication<CurrencyApiKotlinApplication>(*args)
}
