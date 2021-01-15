package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDBUtils;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;

@Slf4j
public class DynamoDBTest {
    @Test
    public void createTable() {
        var dbUtils = new DynamoDBUtils(new PropertiesReader());
        dbUtils.createTable("Repayment", RepaymentItem.ATTRIBUTE_REPAYMENT_ITEM_ID);
    }

    @Test
    public void scan() {
        var dbUtils = new DynamoDBUtils(new PropertiesReader());
        dbUtils.scan().map(Objects::toString).forEach(log::info);
    }
}
