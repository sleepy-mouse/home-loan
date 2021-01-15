package cq.playground.home_loan.dynamodb;

import java.math.BigDecimal;

import static java.math.MathContext.DECIMAL128;

public class DynamoDbItem {
    static BigDecimal round(BigDecimal number) {
        return number.round(DECIMAL128);
    }
}
