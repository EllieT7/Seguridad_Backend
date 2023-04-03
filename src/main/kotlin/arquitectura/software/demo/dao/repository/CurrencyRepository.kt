package arquitectura.software.demo.dao.repository
import arquitectura.software.demo.dao.Currency
import org.springframework.data.repository.CrudRepository

interface CurrencyRepository : CrudRepository<Currency, Long> {
    
}

