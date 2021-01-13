package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDBUtils;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import cq.playground.home_loan.util.PropertiesReader;
import org.junit.jupiter.api.Test;

public class DynamoDBTest {
    @Test
    public void createTable() {
        var dbUtils = new DynamoDBUtils(new PropertiesReader());
        dbUtils.createTable("Repayment", RepaymentItem.ATTRIBUTE_REPAYMENT_ITEM_ID);
    }
}
