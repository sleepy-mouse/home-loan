package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.PriceDutyItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@EqualsAndHashCode(callSuper = false)
@Data
public class PriceDutyPoint extends BaseDomain<PriceDutyItem> {
    private final Integer price;
    private final BigDecimal duty;

    @Override
    public PriceDutyItem dynamoDbItem() {
        var item = new PriceDutyItem();
        item.setPrice(getPrice());
        item.setDuty(getDuty());
        log.info("{}", this);
        return item;
    }
}
