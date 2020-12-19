package cq.playground.home_loan;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static cq.playground.home_loan.PropertyEstablishmentType.ESTABLISHED_HOME;

@Slf4j
public class MainTest {
    @Test
    public void estimateLandTransferDuty() {
        var home = new Home("610000", 2021, 4, 1, ESTABLISHED_HOME);
        var result = LandTransferDutyCalculator.calculate(home);
        log.info("reliefMessage: {}", result.getReliefMessage());
        log.info("duty: {}", result.getDuty());
    }
}
