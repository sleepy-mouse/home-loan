package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDBUtils;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import org.junit.jupiter.api.Test;

public class DynamoDBTest {
    @Test
    public void createTable() {
        DynamoDBUtils.createTable("Repayment", RepaymentItem.ATTRIBUTE_REPAYMENT_ITEM_ID);
    }
}
