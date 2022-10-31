package com.gmail.mbotyuk.parserhtml.scheduling;

import com.gmail.mbotyuk.parserhtml.configuration.EchoBotConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledParsingSite {

    private static final String URL = "https://mironline.ru/support/list/kursy_mir/";
    private static final String X_PATH = "/html/body/div[3]/div[2]/div[1]/div/div/div/div/div[2]/table/tbody/tr[3]/td[2]/span/p";
    private static final BigDecimal VALUE_HUNDRED = BigDecimal.valueOf(100);

    private final EchoBotConfiguration echoBotConfiguration;

    private final Path path = Paths.get("db.txt");
    private final StringBuilder sb = new StringBuilder();
    private String pastValueOfExchangeRate = "";

    @PostConstruct
    private void init() throws IOException {
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException ex) {
            log.warn("File already exists");
        }

        String line = Files.readString(path);

        if (!StringUtils.isBlank(line)) {
            pastValueOfExchangeRate = line;
        }
    }

    @Scheduled(fixedDelay = 50000)
    private void parsing() {
        String newValueOfExchangeRate;
        try {
            Document document = Jsoup.connect(URL).get();
            Elements element = document.selectXpath(X_PATH);
            newValueOfExchangeRate = element.text();
        } catch (IOException e) {
            log.error("Error parsing HTML", e);
            return;
        }

        if (StringUtils.isBlank(newValueOfExchangeRate)) {
            log.warn("New value of exchange rate is blank");
            return;
        }

        if (!pastValueOfExchangeRate.equals(newValueOfExchangeRate)) {
            sb.setLength(0);
            BigDecimal valueOfExchangeRateOfBigDecimal = new BigDecimal(newValueOfExchangeRate.replace(',', '.'));
            sb
                    .append("Курс по карте Мир = ")
                    .append(newValueOfExchangeRate)
                    .append("\n").append("100 RUB = ")
                    .append(VALUE_HUNDRED.divide(valueOfExchangeRateOfBigDecimal, 2, RoundingMode.HALF_UP))
                    .append(" BYN");
            echoBotConfiguration.sendExchangeRateToGroup(sb.toString());
            pastValueOfExchangeRate = newValueOfExchangeRate;

            try {
                Files.writeString(path, newValueOfExchangeRate);
            } catch (IOException e) {
                log.error("Error write to file", e);
            }
        }
    }
}
