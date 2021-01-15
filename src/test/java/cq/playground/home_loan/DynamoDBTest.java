package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDBUtils;
import cq.playground.home_loan.dynamodb.RepaymentItem;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static cq.playground.home_loan.dynamodb.RepaymentItem.TABLE_REPAYMENT;

@Slf4j
public class DynamoDBTest {
    @Test
    public void createTable() {
        var dbUtils = new DynamoDBUtils(new PropertiesReader());
        dbUtils.createTable(TABLE_REPAYMENT, RepaymentItem.CREATE_TABLE_REQUEST);
    }

    @Test
    public void scan() {
        var dbUtils = new DynamoDBUtils(new PropertiesReader());
        dbUtils.scan().map(Objects::toString).forEach(log::info);
    }
}
