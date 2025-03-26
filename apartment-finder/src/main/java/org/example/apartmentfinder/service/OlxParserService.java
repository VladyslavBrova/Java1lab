package org.example.apartmentfinder.service;

import org.example.apartmentfinder.model.Apartment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OlxParserService {
    private final ApartmentService apartmentService;

    @Autowired
    public OlxParserService(@Lazy ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    public void scheduledParsing() {
        try {
            List<Apartment> apartments = parseOlxApartments();
            for (Apartment apartment : apartments) {
                if (apartment.getTitle() == null || apartment.getTitle().isEmpty()) {
                    System.out.println("Помилка: квартира без заголовка! Пропускаємо...");
                    continue;
                }
                apartmentService.saveApartment(apartment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Apartment> parseOlxApartments() throws IOException {
        List<Apartment> apartments = new ArrayList<>();
        String url = "https://www.olx.ua/d/uk/nedvizhimost/kvartiry/prodazha-kvartir/dnepr/";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                .header("Accept-Language", "uk-UA,uk;q=0.9,en;q=0.8")
                .header("Connection", "keep-alive")
                .timeout(10000)
                .get();

        Elements items = doc.select("div[data-cy='l-card']");
        System.out.println("Знайдено оголошень: " + items.size());

        for (Element item : items) {
            try {
                Apartment apartment = new Apartment();

                // Пошук заголовка
                Element titleElement = item.selectFirst("h4");

                if (titleElement != null) {
                    apartment.setTitle(titleElement.text());
                    System.out.println("Знайдено заголовок: " + titleElement.text());
                } else {
                    System.out.println("Помилка: не знайдено заголовок оголошення!");
                    continue;
                }

                // URL оголошення
                Element linkElement = item.selectFirst("a");
                if (linkElement != null) {
                    String olxUrl = "https://www.olx.ua" + linkElement.attr("href");
                    apartment.setOlxUrl(olxUrl);
                    parseApartmentDetails(apartment, olxUrl);
                }

                // Ціна
                Element priceElement = item.selectFirst("p[data-testid='ad-price']");
                if (priceElement != null) {
                    String priceText = priceElement.text().replaceAll("[^0-9]", "");
                    if (!priceText.isEmpty()) {
                        apartment.setPrice(new BigDecimal(priceText));
                    }
                }

                apartment.setPostedDate(LocalDateTime.now());
                apartment.setIsRealtor(false);
                apartments.add(apartment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return apartments;
    }

    private void parseApartmentDetails(Apartment apartment, String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            Element descElement = doc.selectFirst("div[data-cy='ad_description']");
            if (descElement != null) {
                apartment.setDescription(descElement.text());
                String descLower = descElement.text().toLowerCase();
                if (descLower.contains("рієлтор") || descLower.contains("риелтор") ||
                        descLower.contains("агент") || descLower.contains("комісія") ||
                        descLower.contains("комиссия")) {
                    apartment.setIsRealtor(true);
                }
            }

            Element locationElement = doc.selectFirst("p[data-testid='location-date']");
            if (locationElement != null) {
                String location = locationElement.text();
                apartment.setAddress(location);
                Pattern districtPattern = Pattern.compile("(Центр|Тополя|Перемога|Покровський|Шевченківський|Соборний|Чечелівський|Новокодацький|Індустріальний|Самарський)");
                Matcher matcher = districtPattern.matcher(location);
                if (matcher.find()) {
                    apartment.setDistrict(matcher.group(1));
                }
            }

            Elements parameters = doc.select("li[class*='css-']");
            for (Element param : parameters) {
                String paramText = param.text().toLowerCase();
                if (paramText.contains("кількість кімнат") || paramText.contains("количество комнат")) {
                    Matcher matcher = Pattern.compile("\\d+").matcher(paramText);
                    if (matcher.find()) {
                        apartment.setRooms(Integer.parseInt(matcher.group()));
                    }
                } else if (paramText.contains("поверх") || paramText.contains("этаж")) {
                    Matcher matcher = Pattern.compile("(\\d+)(?:.+?(\\d+))?").matcher(paramText);
                    if (matcher.find()) {
                        apartment.setFloor(Integer.parseInt(matcher.group(1)));
                        if (matcher.groupCount() > 1 && matcher.group(2) != null) {
                            apartment.setTotalFloors(Integer.parseInt(matcher.group(2)));
                        }
                    }
                }
            }
            apartment.setContactPhone("Доступно на OLX");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}