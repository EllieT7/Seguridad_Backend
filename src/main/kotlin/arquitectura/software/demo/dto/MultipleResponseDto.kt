import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import arquitectura.software.demo.dto.ResponseDto


@JsonIgnoreProperties(ignoreUnknown = true)
data class MultipleResponseDto(
    var list: List<ResponseDto>,
    var result: BigDecimal)