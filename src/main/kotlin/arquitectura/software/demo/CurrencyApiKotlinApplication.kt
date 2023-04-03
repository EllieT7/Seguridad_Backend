package arquitectura.software.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import arquitectura.software.demo.bl.CompositeCurrencyConverter
import arquitectura.software.demo.bl.SimpleCurrencyConverter
import arquitectura.software.demo.CurrencyConverter
import arquitectura.software.demo.dto.RequestDto
import arquitectura.software.demo.dao.repository.CurrencyRepository
import java.math.BigDecimal



@SpringBootApplication
class CurrencyApiKotlinApplication

fun main(args: Array<String>) {
	runApplication<CurrencyApiKotlinApplication>(*args)
	

    // Crear monedas simples
    val conv1 = SimpleCurrencyConverter(RequestDto("USD", "MXN", BigDecimal(100)))
    val conv2 = SimpleCurrencyConverter(RequestDto("USD", "BOB", BigDecimal(100)))
    val conv3 = SimpleCurrencyConverter(RequestDto("USD", "EUR", BigDecimal(100)))

	conv1.calculate()
	conv2.calculate()
	conv3.calculate()

	// Crear moneda compuesta
	val compositeCurrencyConverter = CompositeCurrencyConverter()
	compositeCurrencyConverter.addCurrencyConverter(conv1)
	compositeCurrencyConverter.addCurrencyConverter(conv2)
	compositeCurrencyConverter.addCurrencyConverter(conv3)

	// Convertir
	// compositeCurrencyConverter.getTotal()
	println(compositeCurrencyConverter.calculate())
}
