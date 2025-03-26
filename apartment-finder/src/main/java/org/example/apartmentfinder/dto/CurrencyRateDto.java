package org.example.apartmentfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateDto {
    private String ccy;     // Код валюти
    private String base_ccy; // Базова валюта
    private BigDecimal buy;  // Курс купівлі
    private BigDecimal sale; // Курс продажу
}