package arquitectura.software.demo.bl

import arquitectura.software.demo.CurrencyConverter
import arquitectura.software.demo.dto.RequestDto
import arquitectura.software.demo.dto.ResponseDto
import arquitectura.software.demo.dto.QueryDto
import java.math.BigDecimal
import java.util.Date

class CompositeCurrencyConverter : CurrencyConverter {
    var total: BigDecimal = BigDecimal.ZERO;
    private val listOfConversions = mutableListOf<CurrencyConverter>()

    fun addCurrencyConverter(currencyConverter: CurrencyConverter) {
        listOfConversions.add(currencyConverter)
    }

    fun removeCurrencyConverter(currencyConverter: CurrencyConverter) {
        listOfConversions.remove(currencyConverter)
    }

    override fun calculate(): BigDecimal {
        
        for (currencyConverter in listOfConversions) {
           
            total += currencyConverter.calculate()
        }
        return total
    }

    override fun showEstructure(): List<ResponseDto> {
        val listOfResponses = mutableListOf<ResponseDto>()
        for (currencyConverter in listOfConversions) {
            listOfResponses.add(currencyConverter.showEstructure().get(0))
        }
        return listOfResponses
    }

}