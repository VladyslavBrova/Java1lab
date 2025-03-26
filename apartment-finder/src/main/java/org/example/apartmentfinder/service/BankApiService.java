package org.example.apartmentfinder.service;

import org.example.apartmentfinder.dto.CurrencyRateDto;
import org.example.apartmentfinder.model.CurrencyRate;
import org.example.apartmentfinder.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class BankApiService {

    @Value("${bank.api.url}")
    private String bankApiUrl;

    private final RestTemplate restTemplate;
    private final CurrencyRateRepository currencyRateRepository;

    @Autowired
    public BankApiService(CurrencyRateRepository currencyRateRepository) {
        this.restTemplate = new RestTemplate();
        this.currencyRateRepository = currencyRateRepository;
    }

    // Оновлювати курси валют кожні 6 годин
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void updateCurrencyRates() {
        try {
            CurrencyRateDto[] rates = restTemplate.getForObject(bankApiUrl, CurrencyRateDto[].class);

            if (rates != null) {
                Arrays.stream(rates).forEach(dto -> {
                    Optional<CurrencyRate> existingRate = currencyRateRepository.findByCurrencyCode(dto.getCcy());

                    if (existingRate.isPresent()) {
                        // Оновлюємо існуючий запис
                        CurrencyRate rate = existingRate.get();
                        rate.setRate(dto.getSale());
                        rate.setUpdatedAt(LocalDateTime.now());
                        currencyRateRepository.save(rate);
                    } else {
                        // Створюємо новий запис
                        CurrencyRate newRate = new CurrencyRate();
                        newRate.setCurrencyCode(dto.getCcy());
                        newRate.setRate(dto.getSale());
                        newRate.setUpdatedAt(LocalDateTime.now());
                        currencyRateRepository.save(newRate);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();

            // Якщо API не доступне, створюємо запасний курс
            if (currencyRateRepository.findByCurrencyCode("USD").isEmpty()) {
                CurrencyRate usdRate = new CurrencyRate();
                usdRate.setCurrencyCode("USD");
                usdRate.setRate(new BigDecimal("39.50")); // Запасний курс долара
                usdRate.setUpdatedAt(LocalDateTime.now());
                currencyRateRepository.save(usdRate);
            }
        }
    }

    public BigDecimal getUsdRate() {
        Optional<CurrencyRate> usdRate = currencyRateRepository.findByCurrencyCode("USD");
        return usdRate.map(CurrencyRate::getRate).orElse(new BigDecimal("39.50"));
    }
}
