package arquitectura.software.demo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorApiDto (
    @JsonProperty("code") var code: String,
    @JsonProperty("message") var message: String
)