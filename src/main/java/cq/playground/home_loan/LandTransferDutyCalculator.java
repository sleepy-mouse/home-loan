package cq.playground.home_loan;

import feign.Feign;
import feign.Logger;
import feign.QueryMap;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.querymap.BeanQueryMapEncoder;
import feign.slf4j.Slf4jLogger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface LandTransferDutyCalculator extends SROCalculator {
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LandTransferDutyCalculator DUTY_CALCULATOR = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new GsonEncoder())
            .decoder(new GsonDecoder())
            .logger(new Slf4jLogger(LandTransferDutyCalculator.class))
            .queryMapEncoder(new BeanQueryMapEncoder())
            .logLevel(Logger.Level.BASIC)
            .target(LandTransferDutyCalculator.class, SRO_CALCULATOR_API_V1);

    private static boolean greaterThan(BigDecimal price, long amount) {
        return price.compareTo(BigDecimal.valueOf(amount)) > 0;
    }

    private static boolean between(BigDecimal price, long start, long end) {
        return price.compareTo(BigDecimal.valueOf(start)) >= 0
                && price.compareTo(BigDecimal.valueOf(end)) <= 0;
    }

    static LandTransferDutyCalculatorResult calculate(
            BigDecimal dutiableValue,
            LocalDate contractDate,
            LocalDate settlementDate,
            PropertyEstablishmentType propertyEstablishmentType) {
        return DUTY_CALCULATOR.calculate(
                new LandTransferDutyCalculatorQueryMap(
                        dutiableValue,
                        false,
                        false,
                        true,
                        true,
                        true,
                        contractDate.format(DATE_TIME_FORMATTER),
                        settlementDate.format(DATE_TIME_FORMATTER),
                        propertyEstablishmentType.name()
                )
        );
    }

    static LandTransferDutyCalculatorResult stampDuty(Home home) {
        return calculate(
                home.getPrice(),
                home.getContractDate(),
                home.getSettlementDate(),
                home.getPropertyEstablishmentType()
        );
    }

    @RequestLine("GET /land-transfer-duty/calculate")
    LandTransferDutyCalculatorResult calculate(
            @QueryMap LandTransferDutyCalculatorQueryMap queryMap
    );
}
