package arquitectura.software.demo
import arquitectura.software.demo.dto.ResponseDto
import arquitectura.software.demo.dto.RequestDto
import java.math.BigDecimal
import org.springframework.stereotype.Service


// Component, el contenedor principal
interface CurrencyConverter {
    // Una funcion en comun es la de convertir una cantidad de una moneda a otra
    // fun convert(requestDto: RequestDto): ResponseDto
    fun calculate(): BigDecimal
    fun showEstructure(): List<ResponseDto>

}