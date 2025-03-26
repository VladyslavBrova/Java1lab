package org.example.apartmentfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String address;
    private String district;
    private Integer rooms;
    private BigDecimal area;
    private Integer floor;
    private Integer totalFloors;
    private String description;
    private String contactPhone;
    private String olxUrl;
    private LocalDateTime postedDate;
    private Boolean isRealtor;
    private BigDecimal priceInUSD;  // Додаткове поле для ціни в доларах
}

