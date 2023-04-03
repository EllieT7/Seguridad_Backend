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
class CurrencyBl @Autowired constructor(private val pagingRepository: PagingRepository) {
    var LOGGER = Logger.getLogger(CurrencyBl::class.java.name)
    
    

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

    fun totalRecords(): Long {
        return pagingRepository.count()
    }
}