package cq.playground.home_loan;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.NumberFormat;

import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
public class LoanRepaymentCalculator {
    private static final NumberFormat CURRENCY_INSTANCE = NumberFormat.getCurrencyInstance();

    private static String format(Number number) {
        return CURRENCY_INSTANCE.format(number);
    }

    public static BigDecimal interestPerRepayment(BigDecimal loanAmount, String annualInterestRate, RepaymentSchedule schedule) {
        return new BigDecimal(annualInterestRate)
                .multiply(loanAmount)
                .divide(BigDecimal.valueOf(schedule.numberOfRepaymentsPerYear()), HALF_UP);
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

    public static BigDecimal repayment(
            BigDecimal loanAmount,
            String annualInterestRate,
            RepaymentSchedule schedule,
            int loanPeriodInYears
    ) {
        var n = loanPeriodInYears * schedule.numberOfRepaymentsPerYear();
        var r = new BigDecimal(annualInterestRate).divide(BigDecimal.valueOf(schedule.numberOfRepaymentsPerYear()), DECIMAL128);
        var pow = ONE.add(r).pow(n);
        var D = pow.subtract(ONE).divide(r.multiply(pow), DECIMAL128);
        return loanAmount.divide(D, HALF_UP);
    }

    public static BigDecimal dailyInterest(
            String offsetBalance,
            String loanAmount,
            String annualInterestRate,
            int year
    ) {
        return new BigDecimal(loanAmount).subtract(new BigDecimal(offsetBalance)).multiply(new BigDecimal(annualInterestRate)).divide(BigDecimal.valueOf(daysInYear(year)), 2, HALF_UP);
    }

    private static int daysInYear(int year) {
        return leapYear(year) ? 366 : 365;
    }

    private static boolean leapYear(int year) {
        if (year % 400 == 0)
            return true;
        if (year % 100 == 0)
            return false;
        return year % 4 == 0;
    }
}
