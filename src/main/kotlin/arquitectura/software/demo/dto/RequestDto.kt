package arquitectura.software.demo.dto


import java.math.BigDecimal

data class RequestDto(
    var from: String,
    var to: String,
    var amount: BigDecimal)