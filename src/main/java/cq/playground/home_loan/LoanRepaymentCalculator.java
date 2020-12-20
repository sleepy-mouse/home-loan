package cq.playground.home_loan;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

@Slf4j
public class LoanRepaymentCalculator {
    private static final NumberFormat CURRENCY_INSTANCE = NumberFormat.getCurrencyInstance();

    private static String format(Number number) {
        return CURRENCY_INSTANCE.format(number);
    }

    public static BigDecimal interestPerRepayment(BigDecimal loanAmount, String annualInterestRate, RepaymentSchedule schedule) {
        return new BigDecimal(annualInterestRate)
                .multiply(loanAmount)
                .divide(BigDecimal.valueOf(schedule.numberOfRepaymentsPerYear()), RoundingMode.HALF_UP);
    }

    public static BigDecimal balanceAfterEachRepayment(BigDecimal loanAmount, BigDecimal interestPerRepayment, BigDecimal repaymentAmount) {
        System.out.println(table("Starting Balance", "Repayment", "Interest Paid", "Principal Paid", "New Balance"));
        var newBalance = loanAmount.subtract(repaymentAmount.subtract(interestPerRepayment));
        System.out.println(table(format(loanAmount), format(repaymentAmount), format(interestPerRepayment), format(repaymentAmount.subtract(interestPerRepayment)), format(newBalance)));
        return newBalance;
    }

    private static String table(Object... args) {
        return String.format("%18s | %-10s | %-15s | %-15s | %-15s", args);
    }
}
