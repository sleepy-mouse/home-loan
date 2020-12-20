package cq.playground.home_loan;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Repayment {
    RepaymentSchedule schedule;
    BigDecimal amount;
}
