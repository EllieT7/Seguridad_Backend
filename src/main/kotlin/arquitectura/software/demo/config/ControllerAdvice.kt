package arquitectura.software.demo.config

import arquitectura.software.demo.dto.ErrorApiDto
import arquitectura.software.demo.dto.ErrorServiceDto
import arquitectura.software.demo.exception.ServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {
    @ExceptionHandler(value = [ServiceException::class])
    fun serviceException(ex: ServiceException): ResponseEntity<ErrorServiceDto> {
        val error = ErrorServiceDto(ErrorApiDto(ex.code, ex.message))
        return ResponseEntity<ErrorServiceDto>(error, HttpStatus.BAD_REQUEST)
    }
}