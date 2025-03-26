package org.example.apartmentfinder.service;

import org.example.apartmentfinder.dto.ApartmentDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    private final ApartmentService apartmentService;

    @Autowired
    public ExcelService(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    public ByteArrayInputStream generateExcel(String district, Integer rooms, Double minPrice, Double maxPrice) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Квартири");

            // Створюємо стилі для заголовків
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Створюємо заголовок
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "ID", "Назва", "Ціна (UAH)", "Ціна (USD)", "Адреса", "Район",
                    "Кімнат", "Площа", "Поверх", "Всього поверхів", "Контакт", "URL"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Отримуємо дані про квартири в залежності від фільтрів
            List<ApartmentDto> apartments;
            if (district != null && !district.isEmpty()) {
                apartments = apartmentService.getApartmentsByDistrict(district);
            } else if (rooms != null) {
                apartments = apartmentService.getApartmentsByRooms(rooms);
            } else if (minPrice != null && maxPrice != null) {
                apartments = apartmentService.getApartmentsByPriceRange(minPrice, maxPrice);
            } else {
                apartments = apartmentService.getAllApartments();
            }

            // Заповнюємо таблицю даними
            int rowIndex = 1;
            for (ApartmentDto apartment : apartments) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(apartment.getId());
                row.createCell(1).setCellValue(apartment.getTitle());

                if (apartment.getPrice() != null) {
                    row.createCell(2).setCellValue(apartment.getPrice().doubleValue());
                }

                if (apartment.getPriceInUSD() != null) {
                    row.createCell(3).setCellValue(apartment.getPriceInUSD().doubleValue());
                }

                row.createCell(4).setCellValue(apartment.getAddress());
                row.createCell(5).setCellValue(apartment.getDistrict());

                if (apartment.getRooms() != null) {
                    row.createCell(6).setCellValue(apartment.getRooms());
                }

                if (apartment.getArea() != null) {
                    row.createCell(7).setCellValue(apartment.getArea().doubleValue());
                }

                if (apartment.getFloor() != null) {
                    row.createCell(8).setCellValue(apartment.getFloor());
                }

                if (apartment.getTotalFloors() != null) {
                    row.createCell(9).setCellValue(apartment.getTotalFloors());
                }

                row.createCell(10).setCellValue(apartment.getContactPhone());
                row.createCell(11).setCellValue(apartment.getOlxUrl());
            }

            // Авторозмір стовпців
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Помилка при створенні Excel-файлу: " + e.getMessage());
        }
    }
}
