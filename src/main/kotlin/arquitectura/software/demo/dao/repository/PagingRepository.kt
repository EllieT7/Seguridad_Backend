package arquitectura.software.demo.dao.repository

import arquitectura.software.demo.dao.Currency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import org.springframework.data.domain.Page
import org.springframework.data.repository.query.Param
import java.util.Date

interface PagingRepository: JpaRepository<Currency, Long> {
    // Filtro por amount
    fun findByAmount(amount: BigDecimal, pageable: Pageable): Page<Currency>;
    // Filtro por currencyFrom
    fun findByCurrencyFrom(currencyFrom: String, pageable: Pageable):Page<Currency>;
    // Filtro por currencyTo
    fun  findByCurrencyTo(currencyTo: String, pageable: Pageable): Page<Currency>;
    // // Filtro por date
    @Query("SELECT c FROM Currency c WHERE extract(year from c.date) = :year AND extract(month from c.date) = :month AND extract(day from c.date) = :day")
    fun  findByDate(@Param("year") year: Int, @Param("month") month: Int, @Param("day") day: Int, pageable: Pageable): Page<Currency>;
}