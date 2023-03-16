package arquitectura.software.demo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorServiceDto (    //Getters and Setters
    @JsonProperty("error")
    var error: ErrorApiDto
)