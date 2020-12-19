package cq.playground.home_loan;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LandTransferDutyCalculatorResult {
    private String reliefMessage;
    private BigDecimal duty;
}
