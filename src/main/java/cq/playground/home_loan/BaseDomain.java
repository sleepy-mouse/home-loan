package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDbItem;

public abstract class BaseDomain<S extends DynamoDbItem<S>> {
    public abstract S dynamoDbItem();
}
