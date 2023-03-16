package arquitectura.software.demo.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal


data class QueryDto(
    @JsonProperty("from") var from: String,
    @JsonProperty("to") var to: String,
    @JsonProperty("amount") var amount: BigDecimal)

