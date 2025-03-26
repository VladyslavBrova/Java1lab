package org.example.apartmentfinder.service;

import org.example.apartmentfinder.dto.ApartmentDto;
import org.example.apartmentfinder.model.Apartment;
import org.example.apartmentfinder.model.CurrencyRate;
import org.example.apartmentfinder.repository.ApartmentRepository;
import org.example.apartmentfinder.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final OlxParserService olxParserService;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository,
                            CurrencyRateRepository currencyRateRepository,
                            @Lazy OlxParserService olxParserService) { // Додаємо @Lazy
        this.apartmentRepository = apartmentRepository;
        this.currencyRateRepository = currencyRateRepository;
        this.olxParserService = olxParserService;
    }

    public List<ApartmentDto> getAllApartments() {
        List<Apartment> apartments = apartmentRepository.findByIsRealtorFalse();
        return convertToDto(apartments);
    }

    public List<ApartmentDto> getApartmentsByDistrict(String district) {
        List<Apartment> apartments = apartmentRepository.findByDistrictContainingIgnoreCaseAndIsRealtorFalse(district);
        return convertToDto(apartments);
    }

    public List<ApartmentDto> getApartmentsByPriceRange(double minPrice, double maxPrice) {
        List<Apartment> apartments = apartmentRepository.findByPriceRangeAndNotRealtor(minPrice, maxPrice);
        return convertToDto(apartments);
    }

    public List<ApartmentDto> getApartmentsByRooms(int rooms) {
        List<Apartment> apartments = apartmentRepository.findByRoomsAndNotRealtor(rooms);
        return convertToDto(apartments);
    }

    public void saveApartment(Apartment apartment) {
        List<Apartment> existingApartments = apartmentRepository.findByOlxUrl(apartment.getOlxUrl());
        if (existingApartments.isEmpty()) {
            apartmentRepository.save(apartment);
        }
    }

    public void saveAllApartments(List<Apartment> apartments) {
        apartmentRepository.saveAll(apartments);
    }

    private List<ApartmentDto> convertToDto(List<Apartment> apartments) {
        Optional<CurrencyRate> usdRate = currencyRateRepository.findByCurrencyCode("USD");
        BigDecimal rate = usdRate.map(CurrencyRate::getRate).orElse(BigDecimal.ONE);

        return apartments.stream()
                .map(apartment -> {
                    ApartmentDto dto = new ApartmentDto();
                    dto.setId(apartment.getId());
                    dto.setTitle(apartment.getTitle());
                    dto.setPrice(apartment.getPrice());
                    dto.setAddress(apartment.getAddress());
                    dto.setDistrict(apartment.getDistrict());
                    dto.setRooms(apartment.getRooms());
                    dto.setArea(apartment.getArea());
                    dto.setFloor(apartment.getFloor());
                    dto.setTotalFloors(apartment.getTotalFloors());
                    dto.setDescription(apartment.getDescription());
                    dto.setContactPhone(apartment.getContactPhone());
                    dto.setOlxUrl(apartment.getOlxUrl());
                    dto.setPostedDate(apartment.getPostedDate());
                    dto.setIsRealtor(apartment.getIsRealtor());

                    if (apartment.getPrice() != null && rate != null) {
                        dto.setPriceInUSD(apartment.getPrice().divide(rate, 2, RoundingMode.HALF_UP));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void manualParseAndSave() throws IOException {
        List<Apartment> apartments = olxParserService.parseOlxApartments();
        for (Apartment apartment : apartments) {
            saveApartment(apartment);
        }
    }
}
