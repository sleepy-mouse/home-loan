package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.DynamoDbUtils;
import cq.playground.home_loan.dynamodb.HomeItem;
import cq.playground.home_loan.util.PropertiesReader;
import org.junit.jupiter.api.Test;

public class HomeTest extends BaseTest {
    @Test
    public void addHome() {
        var dbUtils = new DynamoDbUtils(new PropertiesReader());
        dbUtils.createTable(HomeItem.TABLE_NAME, HomeItem.CREATE_TABLE_REQUEST);
        dbUtils.save(HOME_35_ROYCROFT_AVENUE);
    }
}
