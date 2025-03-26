package org.example.apartmentfinder.repository;

import org.example.apartmentfinder.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    List<Apartment> findByIsRealtorFalse();

    List<Apartment> findByDistrictContainingIgnoreCaseAndIsRealtorFalse(String district);

    @Query("SELECT a FROM Apartment a WHERE a.price BETWEEN :minPrice AND :maxPrice AND a.isRealtor = false")
    List<Apartment> findByPriceRangeAndNotRealtor(double minPrice, double maxPrice);

    @Query("SELECT a FROM Apartment a WHERE a.rooms = :rooms AND a.isRealtor = false")
    List<Apartment> findByRoomsAndNotRealtor(int rooms);

    List<Apartment> findByOlxUrl(String olxUrl);
}

