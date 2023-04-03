package arquitectura.software.demo.bl
import arquitectura.software.demo.CurrencyConverter
import arquitectura.software.demo.dao.Currency
import arquitectura.software.demo.dao.repository.CurrencyRepository
import arquitectura.software.demo.dto.ErrorServiceDto
import arquitectura.software.demo.dto.RequestDto
import arquitectura.software.demo.dto.ResponseDto
import arquitectura.software.demo.exception.ServiceException
import arquitectura.software.demo.CurrencyApiKotlinApplication
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.PropertySource
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.*

class SimpleCurrencyConverter(val requestDto: RequestDto): CurrencyConverter{
 
    lateinit var currencyRepository: CurrencyRepository;
    fun setCurrecyRepository(currencyRepository: CurrencyRepository) {
        
        this.currencyRepository = currencyRepository
    }
 
    var LOGGER = Logger.getLogger(CurrencyBl::class.java.name)

    override fun calculate(): BigDecimal {
        //Llamamos a nuestro Bl
        //val currencyBl = CurrencyBl(pagingRepository: PagingRepository , CurrencyRepository: CurrencyRepository)
        return convert(requestDto).result;
    }

    override fun showEstructure(): List<ResponseDto> {
        //Lista del elemento simple
        val list: MutableList<ResponseDto> = mutableListOf()
        list.add(convert(requestDto))
        return list
    }

    /**
     * Método que convierte una moneda a otra
     * @param requestDto
     * @return
     */
    fun convert(requestDto: RequestDto): ResponseDto {
        if (requestDto.amount < (BigDecimal.ZERO)) {
            LOGGER.log(Level.WARNING, "No se puede convertir una cantidad menor a 0")
            throw ServiceException("No se puede convertir una cantidad menor a 0", "bad_amount")
        }
        LOGGER.info("Convirtiendo ${requestDto.amount} ${requestDto.from} a ${requestDto.to}")
        val response: Response = invokeApi("https://api.apilayer.com/exchangerates_data/convert?to=${requestDto.to}&from=${requestDto.from}&amount=${requestDto.amount}")

        if (response.isSuccessful) {
            //Obtenemos la respuesta exitosa
            val responseDto: ResponseDto = parseResponse(response)
            LOGGER.info("Respuesta de la API: $responseDto")
            //Si es exitoso guardo en bd
            currencyRepository.save(Currency(requestDto.from, requestDto.to, requestDto.amount, responseDto.result, Date()))
            //servicio.guardar(Currency(requestDto.from, requestDto.to, requestDto.amount, responseDto.result, Date()))
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
    public fun invokeApi(endpoint: String): Response {
        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val request: Request = Request.Builder()
            .url(endpoint)
            .addHeader("apikey", "DKtvGE5j3a4vFH9qUICssZzdVdKJIla9")
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
    public fun parseResponse(response: Response): ResponseDto {
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
    public fun parseError(response: Response): ErrorServiceDto {
        val mapper = jacksonObjectMapper()
        try {
            return mapper.readValue(response.body?.string(), ErrorServiceDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.info("Error al parsear el error de la API")
            throw ServiceException("Error interno, parseo", "internal_error")
        }
    }


    


/*
    public fun getInstanceOfCurrencyRepository(): CurrencyRepository {
        val currencyRepository1 = object: CurrencyRepository {
            override fun <S : Currency?> save(entity: S): S {
                TODO("Not yet implemented")
            }

            override fun <S : Currency?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
                TODO("Not yet implemented")
            }

            override fun findAll(): MutableIterable<Currency> {
                TODO("Not yet implemented")
            }

            override fun findAllById(ids: MutableIterable<Long>): MutableIterable<Currency> {
                TODO("Not yet implemented")
            }

            override fun count(): Long {
                TODO("Not yet implemented")
            }

            override fun delete(entity: Currency) {
                TODO("Not yet implemented")
            }

            override fun deleteAllById(ids: MutableIterable<Long>) {
                TODO("Not yet implemented")
            }

            override fun deleteAll(entities: MutableIterable<Currency>) {
                TODO("Not yet implemented")
            }

            override fun deleteAll() {
                TODO("Not yet implemented")
            }

            override fun deleteById(id: Long) {
                TODO("Not yet implemented")
            }

            override fun existsById(id: Long): Boolean {
                TODO("Not yet implemented")
            }

            override fun findById(id: Long): Optional<Currency> {
                TODO("Not yet implemented")
            }
        }
        return currencyRepository1

    }*/
}