package arquitectura.software.demo.bl

import arquitectura.software.demo.dao.Currency
import arquitectura.software.demo.dto.ErrorServiceDto
import arquitectura.software.demo.dto.RequestDto
import arquitectura.software.demo.dto.ResponseDto
import arquitectura.software.demo.dao.repository.CurrencyRepository
import arquitectura.software.demo.dao.repository.PagingRepository
import arquitectura.software.demo.exception.ServiceException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.Date
import java.util.logging.Level
import java.util.logging.Logger
import java.util.Calendar
import java.text.SimpleDateFormat

//Autowired para la bd
@Service
class CurrencyBl @Autowired constructor(private val currencyRepository: CurrencyRepository, private val pagingRepository: PagingRepository) {
    var LOGGER = Logger.getLogger(CurrencyBl::class.java.name)

    //Declaramos la api key
    @Value("\${api.key}")
    private val apiKey: String = ""
    //Declaramos la url de la api
    @Value("\${api.url}")
    private val apiUrl: String = ""

    /**
     * Método que convierte una moneda a otra
     * @param requestDto
     * @return
     */
    fun convert(requestDto: RequestDto): ResponseDto {
        //Verificamos que el monto sea mayor a 0
        if (requestDto.amount < (BigDecimal.ZERO)) {
            LOGGER.log(Level.WARNING, "No se puede convertir una cantidad menor a 0")
            throw ServiceException("No se puede convertir una cantidad menor a 0", "bad_amount")
        }
        LOGGER.info("Convirtiendo ${requestDto.amount} ${requestDto.from} a ${requestDto.to}")
        val response: Response = invokeApi("${apiUrl}?to=${requestDto.to}&from=${requestDto.from}&amount=${requestDto.amount}")

        if (response.isSuccessful) {
            //Obtenemos la respuesta exitosa
            val responseDto: ResponseDto = parseResponse(response)
            LOGGER.info("Respuesta de la API: $responseDto")
            //Si es exitoso guardo en bd
            currencyRepository.save(Currency(requestDto.from, requestDto.to, requestDto.amount, responseDto.result, Date()))
            LOGGER.info("Respuesta guardada en bd correctamente")
            return responseDto
        } else {
            //Obtenemos el error
            val errorServiceDto: ErrorServiceDto = parseError(response)
            LOGGER.log(Level.WARNING, "Error de la API: ${errorServiceDto.error}")
            throw ServiceException(errorServiceDto.error.message, errorServiceDto.error.code)
        }
    }

    /**
     * Método que invoca la API de conversión de monedas
     * okhtttp3
     * @param endpoint
     * @return Response
     */
    private fun invokeApi(endpoint: String): Response {
        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val request: Request = Request.Builder()
            .url(endpoint)
            .addHeader("apikey", apiKey)
            .method("GET", null)
            .build()
        try {
            return client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.log(Level.WARNING, "Error al invocar la API")
            throw ServiceException("Error al invocar la API", "timeout")
        }
    }

    /**
     * Método que parsea la respuesta de la API a un responseDto
     * @param response
     * @return ResponseDto
     */
    private fun parseResponse(response: Response): ResponseDto {
        val mapper = jacksonObjectMapper()
        try {
            return mapper.readValue(response.body?.string(), ResponseDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.info("Error al parsear la respuesta de la API")
            throw ServiceException("Error interno, parseo", "internal_error")
        }
    }

    /**
     * Método que parsea el error de la API a un errorServiceDto
     * @param response
     * @return ErrorServiceDto
     */
    private fun parseError(response: Response): ErrorServiceDto {
        val mapper = jacksonObjectMapper()
        try {
            return mapper.readValue(response.body?.string(), ErrorServiceDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.info("Error al parsear el error de la API")
            throw ServiceException("Error interno, parseo", "internal_error")
        }
    }

    /**
     * Método que obtiene el historial de conversiones
     * @return List<Currency>
     */
    fun history(page: Int, size: Int, filter: MutableMap<String, Any>): List<Currency> {
        LOGGER.info("Obteniendo historial de conversiones: page=$page, size=$size, filter=$filter")
        val pageable: Pageable = PageRequest.of(page, size, Sort.by("date").descending());
        val list: List<Currency>;
        // return pagingRepository.findAll(pageable).toList()    
        // return pagingRepository.findByCurrencyFrom("bob", pageable).toList();
        //Analizamos filtros
        // Si la lista filters es vacia, devolvemos todos los registros
        if (filter.isEmpty()) {
            list = pagingRepository.findAll(pageable).toList()
        } else {
            // Si la lista filters no es vacia, devolvemos los registros que cumplan con los filtros
            // Se obtiene el primer filtro
            val firstFilter: Map.Entry<String, Any> = filter.entries.first()
            // Se obtiene el valor del filtro
            val value: Any = firstFilter.value
            // Se obtiene el nombre del filtro
            val name: String = firstFilter.key
            // Clasificacion de keys
            var aux: Date = Date();
            var cal: Calendar = Calendar.getInstance();
            if(name == "date"){

                aux = SimpleDateFormat("yyyy-MM-dd").parse(value.toString())
                cal.time = aux
            }
            when (name) {
                "from" -> list = pagingRepository.findByCurrencyFrom(value.toString(), pageable).toList()
                "to" -> list = pagingRepository.findByCurrencyTo(value.toString(), pageable).toList()
                "amount" -> list = pagingRepository.findByAmount(value.toString().toBigDecimal(), pageable).toList()
                "date" -> list = pagingRepository.findByDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH), pageable).toList()
                else -> list = pagingRepository.findAll(pageable).toList()
            }
        }
        return list;
    }
}