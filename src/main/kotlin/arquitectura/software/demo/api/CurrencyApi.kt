package arquitectura.software.demo.api

import arquitectura.software.demo.bl.CurrencyBl
import arquitectura.software.demo.dto.RequestDto
import arquitectura.software.demo.dto.ResponseDto
import arquitectura.software.demo.dao.Currency
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
// import org.springframework.security.access.prepost.PreAuthorize
import java.math.BigDecimal
import java.util.logging.Level
import java.util.logging.Logger
import java.util.Date
import java.security.Principal

@RestController
@RequestMapping("/api/currency")

class CurrencyApi (private val currencyBl: CurrencyBl) {

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
        //Log procesando solicitud
        LOGGER.log(
            Level.INFO,
            "Procesando solicitud de conversion de moneda: ${requestDto.amount} ${requestDto.from}  a ${requestDto.to}"
        )
        return currencyBl.convert(requestDto)
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


    companion object {
        private val LOGGER = Logger.getLogger(CurrencyApi::class.java.name)
    }
}