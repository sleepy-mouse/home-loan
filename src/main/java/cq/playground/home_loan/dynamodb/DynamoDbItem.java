package cq.playground.home_loan.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.math.BigDecimal;

import static java.math.MathContext.DECIMAL128;

public abstract class DynamoDbItem<S> {
    static BigDecimal round(BigDecimal number) {
        return number.round(DECIMAL128);
    }

    public abstract String getTableName();

    public abstract TableSchema<S> getTableSchema();
}
