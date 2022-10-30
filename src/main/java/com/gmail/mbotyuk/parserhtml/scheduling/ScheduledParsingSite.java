package com.gmail.mbotyuk.parserhtml.scheduling;

import com.gmail.mbotyuk.parserhtml.configuration.EchoBotConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ScheduledParsingSite {

    private static final String URL = "https://mironline.ru/support/list/kursy_mir/";
    private static final String X_PATH = "/html/body/div[3]/div[2]/div[1]/div/div/div/div/div[2]/table/tbody/tr[3]/td[2]/span/p";
    private static final BigDecimal VALUE_HUNDRED = BigDecimal.valueOf(100);

    private final EchoBotConfiguration echoBotConfiguration;

    private final StringBuilder sb = new StringBuilder();
    private Document document;
    private Elements element;
    private String pastValueOfExchangeRate = "";

    @SneakyThrows
    @Scheduled(fixedDelay = 60000)
    private void parsing() {
        document = Jsoup.connect(URL).get();
        element = document.selectXpath(X_PATH);
        String newValueOfExchangeRate = element.text();

        if (!pastValueOfExchangeRate.equals(newValueOfExchangeRate)) {
            sb.setLength(0);
            BigDecimal valueOfExchangeRateOfBigDecimal = new BigDecimal(newValueOfExchangeRate.replace(',', '.'));
            sb.append("Курс по карте Мир = " + newValueOfExchangeRate)
                    .append("\n")
                    .append("100 RUB = " + VALUE_HUNDRED.divide(valueOfExchangeRateOfBigDecimal, 2, RoundingMode.HALF_UP))
                    .append(" BYN");
            pastValueOfExchangeRate = newValueOfExchangeRate;
            echoBotConfiguration.sendExchangeRateToGroup(sb.toString());
        }
    }
}
