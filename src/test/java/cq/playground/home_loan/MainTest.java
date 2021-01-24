package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDbUtils;
import cq.playground.home_loan.dynamodb.HomeItem;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cq.playground.home_loan.LandTransferDutyCalculator.stampDuty;
import static cq.playground.home_loan.LoanRepaymentCalculator.balanceAfterEachRepayment;
import static cq.playground.home_loan.LoanRepaymentCalculator.interestPerRepayment;
import static cq.playground.home_loan.PropertyEstablishmentType.ESTABLISHED_HOME;
import static cq.playground.home_loan.RepaymentSchedule.MONTHLY;
import static cq.playground.home_loan.dynamodb.PriceDutyItem.CREATE_TABLE_REQUEST;
import static cq.playground.home_loan.dynamodb.PriceDutyItem.TABLE_PRICE_DUTY;
import static cq.playground.home_loan.dynamodb.RepaymentItem.TABLE_REPAYMENT;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class MainTest {
    private static final Home HOME_14_PEYTON_DRIVE = Home.build("14 Peyton Drive, Mill Park, VIC 3082", "780000", 2021, 6, 1, ESTABLISHED_HOME);
    private static final Home HOME_16_WARBURTON_COURT = Home.build("16 Warburton Court, Mill Park, VIC 3082", "720000", 2021, 6, 1, ESTABLISHED_HOME);
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static void delimiter(int counter, RepaymentSchedule repaymentSchedule) {
        System.out.printf("%3d *************************************************************************************************** %s%n", counter, repaymentSchedule.getDescription());
    }

    private static void delimiter() {
        System.out.printf("*************************************************************************************************** %n");
    }

    @Test
    public void landTransferDuty() {
        var dbUtils = new DynamoDbUtils(new PropertiesReader());
        dbUtils.createTable(TABLE_PRICE_DUTY, CREATE_TABLE_REQUEST);
        var points = IntStream.iterate(600000, operand -> operand <= 1000000, operand -> operand + 1000)
                .mapToObj(price -> {
                    try {
                        var home = new Home("", Integer.toString(price), 2021, 5, 1, ESTABLISHED_HOME);
                        var future = executorService.schedule(() -> stampDuty(home), 5, SECONDS);
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
    public void addHome() {
        var dbUtils = new DynamoDbUtils(new PropertiesReader());
        dbUtils.createTable(HomeItem.TABLE_NAME, HomeItem.CREATE_TABLE_REQUEST);
        dbUtils.save(HOME_14_PEYTON_DRIVE);
    }

    @Test
    public void estimateLandTransferDuty() {
        var result = stampDuty(HOME_16_WARBURTON_COURT);
        log.info("reliefMessage: {}", result.getReliefMessage());
        log.info("duty: {}", result.getDuty());
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
        var interestPerRepayment = BigDecimal.ZERO;
        var counter = 0;
        while (balanceAfterEachRepayment.compareTo(BigDecimal.ZERO) > 0) {
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
    public void repayment() {
        var deposit = new BigDecimal("220000");
        var annualInterestRate = "0.023";
        var loanAmount = HOME_16_WARBURTON_COURT.price.subtract(deposit);
        log.info("Loan Amount: {}", loanAmount);
        delimiter();
        var repayment = LoanRepaymentCalculator.repayment(loanAmount, annualInterestRate, MONTHLY, 30);
        log.info("Repayment: {}", repayment);
    }
}
