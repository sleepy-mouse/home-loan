package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDbUtils;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cq.playground.home_loan.LandTransferDutyCalculator.stampDuty;
import static cq.playground.home_loan.PropertyEstablishmentType.ESTABLISHED_HOME;
import static cq.playground.home_loan.dynamodb.PriceDutyItem.CREATE_TABLE_REQUEST;
import static cq.playground.home_loan.dynamodb.PriceDutyItem.TABLE_PRICE_DUTY;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class StampDutyTest extends BaseTest {
    @Test
    public void landTransferDuty() {
        var dbUtils = new DynamoDbUtils(new PropertiesReader());
        dbUtils.createTable(TABLE_PRICE_DUTY, CREATE_TABLE_REQUEST);
        var points = IntStream.iterate(600000, operand -> operand <= 1000000, operand -> operand + 1000)
                .mapToObj(price -> {
                    try {
                        var home = new Home("", Integer.toString(price), 2021, 5, 1, ESTABLISHED_HOME);
                        var future = EXECUTOR_SERVICE.schedule(() -> stampDuty(home), 5, SECONDS);
                        var result = future.get();
                        return new PriceDutyPoint(price, result.getDuty());
                    } catch (Exception e) {
                        log.error("", e);
                        return null;
                    }
                })
                .collect(Collectors.toList());
        points.forEach(dbUtils::save);
        points.stream().map(Objects::toString).forEach(log::info);
    }

    @Test
    public void estimateLandTransferDuty() {
        var result = stampDuty(HOME_35_ROYCROFT_AVENUE);
        log.info("reliefMessage: {}", result.getReliefMessage());
        log.info("duty: {}", result.getDuty());
    }
}
