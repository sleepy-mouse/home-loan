package cq.playground.home_loan;

import com.fasterxml.uuid.Generators;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
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

    public static Repayment build(
            BigDecimal loanBalance,
            BigDecimal repaymentAmount,
            BigDecimal interestPaid,
            RepaymentSchedule schedule
    ) {
        return new Repayment(
                loanBalance,
                repaymentAmount,
                interestPaid,
                schedule
        );
    }

    public RepaymentItem dynamoDbItem() {
        var uuid = Generators.randomBasedGenerator().generate();
        log.info("UUID: {}", uuid);
        log.info("UUID version: {}", uuid.version());
        log.info("UUID variant: {}", uuid.variant());

        var item = new RepaymentItem();
        item.setRepaymentItemId(uuid.toString());
        item.setLoanBalance(getLoanBalance());
        item.setRepaymentAmount(getRepaymentAmount());
        item.setInterestPaid(getInterestPaid());
        item.setPrincipalPaid(getPrincipalPaid());
        item.setSchedule(getSchedule());
        log.info("{}", this);
        return item;
    }
}
