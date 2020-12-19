package cq.playground.home_loan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LandTransferDutyCalculatorQueryMap {
    BigDecimal dutiableValue;
    boolean regComm;
    boolean foreignPurchaser;
    boolean residentialProperty;
    boolean principalPlaceOfResidence;
    boolean firstHomeOwner;
    String contractDate;
    String settlementDate;
    String propertyEstablishmentType;
}
