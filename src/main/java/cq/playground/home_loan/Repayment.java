package cq.playground.home_loan;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Repayment {
    final BigDecimal loanBalance;
    final BigDecimal repaymentAmount;
    final BigDecimal interestPaid;
    final BigDecimal principalPaid;
    final RepaymentSchedule schedule;

    public Repayment(BigDecimal loanBalance,
                     BigDecimal repaymentAmount,
                     BigDecimal interestPaid,
                     RepaymentSchedule schedule) {
        this.loanBalance = loanBalance;
        this.repaymentAmount = repaymentAmount;
        this.interestPaid = interestPaid;
        this.principalPaid = repaymentAmount.subtract(interestPaid);
        this.schedule = schedule;
    }
}
