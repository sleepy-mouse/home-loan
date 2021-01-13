package cq.playground.home_loan.dynamodb;

import cq.playground.home_loan.Repayment;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.net.URI;

import static cq.playground.home_loan.dynamodb.RepaymentItem.ATTRIBUTE_REPAYMENT_ITEM_ID;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Slf4j
public class DynamoDBUtils {
    private static final TableSchema<RepaymentItem> TABLE_SCHEMA_REPAYMENT =
            StaticTableSchema.builder(RepaymentItem.class).newItemSupplier(RepaymentItem::new)
                    .addAttribute(String.class, a -> a.name(ATTRIBUTE_REPAYMENT_ITEM_ID)
                            .getter(RepaymentItem::getRepaymentItemId)
                            .setter(RepaymentItem::setRepaymentItemId)
                            .tags(primaryPartitionKey()))
                    .build();

    public static void createTable(String tableName, String key) {
        var dbClient = getDbClient();
        try (var dbWaiter = dbClient.waiter()) {
            var request = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(key)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(key)
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
                    .tableName(tableName)
                    .build();

            var response = dbClient.createTable(request);
            var tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            // Wait until the Amazon DynamoDB table is created
            var waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            log.info("Table: {}", response.tableDescription().tableName());
        } catch (DynamoDbException e) {
            log.error("", e);
        }
    }

    public static void save(Repayment repayment) {
        var dbClient = getEnhancedDbClient();
        var mappedTable = dbClient.table("RepaymentRecord", TABLE_SCHEMA_REPAYMENT);
        var item = repayment.generateItem();
        var enReq = PutItemEnhancedRequest.builder(RepaymentItem.class)
                .item(item)
                .build();
        mappedTable.putItem(enReq);
    }

    private static DynamoDbEnhancedClient getEnhancedDbClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDbClient())
                .build();
    }

    private static DynamoDbClient getDbClient() {
        var endpoint = "http://localhost:8000";
        var region = Region.US_EAST_1;
        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
