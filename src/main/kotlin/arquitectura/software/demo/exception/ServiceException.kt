package arquitectura.software.demo.exception

data class ServiceException(override var message: String, val code: String) : RuntimeException(message)