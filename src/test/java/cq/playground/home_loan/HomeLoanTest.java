package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDbUtils;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static cq.playground.home_loan.LandTransferDutyCalculator.stampDuty;
import static cq.playground.home_loan.LoanRepaymentCalculator.*;
import static cq.playground.home_loan.RepaymentSchedule.MONTHLY;
import static cq.playground.home_loan.dynamodb.RepaymentItem.TABLE_REPAYMENT;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class HomeLoanTest extends BaseTest {
    @Test
    public void repayment() {
        var deposit = new BigDecimal("220000");
        var annualInterestRate = "0.023";
        var loanAmount = HOME_16_WARBURTON_COURT.price.subtract(deposit);
        log.info("Loan Amount: {}", loanAmount);
        delimiter();
        var repayment = LoanRepaymentCalculator.repayment(loanAmount, annualInterestRate, MONTHLY, 30);
        log.info("Repayment: {}", repayment);
    }

    @Test
    public void calculateRepayments() {
        var home = HOME_16_WARBURTON_COURT;
        var savings = new BigDecimal("250000");
        var annualInterestRate = "0.0230";
        var repaymentAmount = new BigDecimal("2000");
        var repaymentSchedule = MONTHLY;
        var result = stampDuty(home);
        var stampDuty = result.getDuty();
        log.info(String.format("Stamp duty: %s", stampDuty));
        var repayments = new ArrayList<Repayment>(200);
        var loanAmount = home.price.subtract(savings.subtract(stampDuty));
        log.info("Loan Amount: {}", loanAmount);
        var balanceAfterEachRepayment = loanAmount;
        var interestPerRepayment = ZERO;
        var counter = 0;
        while (balanceAfterEachRepayment.compareTo(ZERO) > 0) {
            delimiter(++counter, repaymentSchedule);
            interestPerRepayment = interestPerRepayment(balanceAfterEachRepayment, annualInterestRate, repaymentSchedule);
            repayments.add(Repayment.build(balanceAfterEachRepayment, repaymentAmount, interestPerRepayment, repaymentSchedule));
            balanceAfterEachRepayment = balanceAfterEachRepayment(balanceAfterEachRepayment, interestPerRepayment, repaymentAmount);
        }
        delimiter();
        System.out.printf("%6s: %d%n", "Years", counter / 12);
        System.out.printf("%6s: %d%n", "Months", counter % 12);
//        save(repayments);
    }

    private void save(List<Repayment> repayments) {
        var dbUtils = new DynamoDbUtils(new PropertiesReader());
        dbUtils.createTable(TABLE_REPAYMENT, RepaymentItem.CREATE_TABLE_REQUEST);
        repayments.forEach(dbUtils::save);
    }

    @Test
    public void dailyInterestWithOffset() {
        var principal = "500000";
        var offsetBalance = "30000";
        var annualInterestRate = "0.0299";
        int year = 2021;
        var dailyInterest = dailyInterest(offsetBalance, principal, annualInterestRate, year);
        assertEquals(dailyInterest, new BigDecimal("38.50"));

        offsetBalance = "0";
        dailyInterest = dailyInterest(offsetBalance, principal, annualInterestRate, year);
        assertEquals(dailyInterest, new BigDecimal("40.96"));
    }
}
