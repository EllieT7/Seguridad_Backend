package arquitectura.software.demo.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResponseDto(
    var success: Boolean,
    var query: QueryDto,
    var date: String,
    var result: BigDecimal )
