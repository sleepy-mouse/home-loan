package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDBUtils;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static cq.playground.home_loan.LoanRepaymentCalculator.balanceAfterEachRepayment;
import static cq.playground.home_loan.LoanRepaymentCalculator.interestPerRepayment;
import static cq.playground.home_loan.PropertyEstablishmentType.ESTABLISHED_HOME;
import static cq.playground.home_loan.RepaymentSchedule.MONTHLY;

@Slf4j
public class MainTest {
    private static final Home HOME1 = new Home("600000", 2021, 6, 1, ESTABLISHED_HOME);

    private static void delimiter(int counter, RepaymentSchedule repaymentSchedule) {
        System.out.printf("%3d *************************************************************************************************** %s%n", counter, repaymentSchedule.getDescription());
    }

    private static void delimiter() {
        System.out.printf("*************************************************************************************************** %n");
    }

    @Test
    public void estimateLandTransferDuty() {
        var result = LandTransferDutyCalculator.stampDuty(HOME1);
        log.info("reliefMessage: {}", result.getReliefMessage());
        log.info("duty: {}", result.getDuty());
    }

    @Test
    public void calculateRepayments() {
        var savings = new BigDecimal("250000");
        var annualInterestRate = "0.0230";
        var repaymentAmount = new BigDecimal("2000");
        var repaymentSchedule = MONTHLY;
        var result = LandTransferDutyCalculator.stampDuty(HOME1);
        var stampDuty = result.getDuty();
        System.out.printf("Stamp duty: %s%n", stampDuty);
        var repayments = new ArrayList<Repayment>(200);
        var loanAmount = HOME1.price.subtract(savings.subtract(stampDuty));
        var balanceAfterEachRepayment = loanAmount;
        var interestPerRepayment = BigDecimal.ZERO;
        var counter = 0;
        while (balanceAfterEachRepayment.compareTo(BigDecimal.ZERO) > 0) {
            delimiter(++counter, repaymentSchedule);
            interestPerRepayment = interestPerRepayment(balanceAfterEachRepayment, annualInterestRate, repaymentSchedule);
            repayments.add(new Repayment(balanceAfterEachRepayment, repaymentAmount, interestPerRepayment, repaymentSchedule));
            balanceAfterEachRepayment = balanceAfterEachRepayment(balanceAfterEachRepayment, interestPerRepayment, repaymentAmount);
        }
        delimiter();
        System.out.printf("%6s: %d%n", "Years", counter / 12);
        System.out.printf("%6s: %d%n", "Months", counter % 12);
        save(repayments);
    }

    private void save(List<Repayment> repayments) {
        var dbUtils = new DynamoDBUtils(new PropertiesReader());
        repayments.forEach(dbUtils::save);
    }
}
