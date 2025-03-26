package org.example.apartmentfinder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "apartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private BigDecimal price;

    private String address;

    private String district;

    private Integer rooms;

    private BigDecimal area;

    private Integer floor;

    private Integer totalFloors;

    @Column(columnDefinition = "CLOB")
    private String description;

    private String contactPhone;

    private String olxUrl;

    private LocalDateTime postedDate;

    private Boolean isRealtor = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}

