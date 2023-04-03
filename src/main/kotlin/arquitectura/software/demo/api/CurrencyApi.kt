package arquitectura.software.demo.api

import arquitectura.software.demo.bl.CurrencyBl
import arquitectura.software.demo.bl.SimpleCurrencyConverter
import arquitectura.software.demo.bl.CompositeCurrencyConverter
import arquitectura.software.demo.dto.RequestDto
import arquitectura.software.demo.dto.ResponseDto
import arquitectura.software.demo.dao.Currency
import arquitectura.software.demo.dao.repository.CurrencyRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.math.BigDecimal
import java.util.logging.Level
import java.util.logging.Logger
import java.util.Date
import java.security.Principal
import java.net.URLDecoder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import MultipleResponseDto


@RestController
@RequestMapping("/api/currency")

class CurrencyApi (private val currencyBl: CurrencyBl, private val currencyRepository: CurrencyRepository) {

    /**
     * Endpoint GET para obtener la conversión de una moneda a otra
     */
    @GetMapping("/convert")
    fun convert(
        @RequestParam from: String,
        @RequestParam to: String,
        @RequestParam amount: BigDecimal
    ): ResponseDto {
        
        val requestDto = RequestDto(from, to, amount)
        val simpleCurrencyConverter = SimpleCurrencyConverter(requestDto)
        simpleCurrencyConverter.setCurrecyRepository(currencyRepository)
        //Log procesando solicitud
        LOGGER.log(
            Level.INFO,
            "Procesando solicitud de conversion de moneda: ${requestDto.amount} ${requestDto.from}  a ${requestDto.to}"
        )
        return simpleCurrencyConverter.convert(requestDto)
    }

    @GetMapping("/multiple")
    fun multiple(@RequestParam q: String): MultipleResponseDto {

        //Decodificamos
        val decodedJson: String = URLDecoder.decode(q, "UTF-8")
        //Convertimos a lista de objetos
        val listType = object : TypeToken<List<RequestDto>>() {}.type
        val requestDtoList: List<RequestDto> = Gson().fromJson(decodedJson, listType)
        //Log procesando solicitud
        LOGGER.log(
            Level.INFO,
            "Procesando solicitud de conversion de moneda: ${requestDtoList}"
        )
        //Añadimos los convertidores simples a un convertidor compuesto
        var compositeCurrencyConverter = CompositeCurrencyConverter()
        
        for (requestDto in requestDtoList) {
            val simpleCurrencyConverter = SimpleCurrencyConverter(requestDto)
            simpleCurrencyConverter.setCurrecyRepository(currencyRepository)
            compositeCurrencyConverter.addCurrencyConverter(simpleCurrencyConverter)
        }
        var multipleResponseDto = MultipleResponseDto(compositeCurrencyConverter.showEstructure(), compositeCurrencyConverter.calculate())
        return multipleResponseDto
    }


    // @GetMapping("/user")
    // @PreAuthorize("hasAuthority('ROLE_USER')")
    // fun user(): String{
    //     LOGGER.log(Level.INFO, "Procesando solicitud de usuario")
    //     return "ROLE_USER"
    // }

    // @GetMapping("/admin")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // fun admin(): String{
    //     LOGGER.log(Level.INFO, "Procesando solicitud de administrador")
    //     return "ROLE_ADMIN"
    // }

    // //Principal tiene informacion del usuario
    // @GetMapping("/principal")
    // fun info(principal: Principal): String{
    //     return principal.toString()
    // }

    /** 
     * Endpoint GET para obtener el historial de conversiones
    */
    @GetMapping("/history")
    fun history(
        @RequestParam(defaultValue = "1") page: Int, 
        @RequestParam(defaultValue = "5") size: Int,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
        @RequestParam(required = false) amount: BigDecimal?,
        @RequestParam(required = false) date: String? ): List<Currency> {
        //Log procesando solicitud
        LOGGER.log(Level.INFO, "Procesando solicitud de historial de conversiones")
        // Si se envian parametros de busqueda, se filtran los resultados
        // Se guardan los filtros validos en un mapa
        val filters = mutableMapOf<String, Any>()
        if (from != null) filters["from"] = from
        if (to != null) filters["to"] = to
        if (amount != null) filters["amount"] = amount
        if (date != null) filters["date"] = date
        return currencyBl.history(page, size, filters);
    }
    
    @GetMapping("/totalRecords")
    fun totalRecords(): Long {
        //Log procesando solicitud
        LOGGER.log(Level.INFO, "Procesando solicitud de total de registros")
        return currencyBl.totalRecords();
    }

    companion object {
        private val LOGGER = Logger.getLogger(CurrencyApi::class.java.name)
    }
}