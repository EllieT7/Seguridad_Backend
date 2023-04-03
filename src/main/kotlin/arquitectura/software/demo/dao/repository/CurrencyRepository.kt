package arquitectura.software.demo.dao.repository
import arquitectura.software.demo.dao.Currency
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Bean

@Service
interface CurrencyRepository : CrudRepository<Currency, Long> {
    
}

