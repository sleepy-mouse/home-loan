package cq.playground.home_loan.dynamodb;

import cq.playground.home_loan.Repayment;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cq.playground.home_loan.dynamodb.RepaymentItem.TABLE_REPAYMENT;
import static cq.playground.home_loan.dynamodb.RepaymentItem.TABLE_SCHEMA_REPAYMENT;

@Slf4j
public class DynamoDBUtils {
    private final PropertiesReader propertiesReader;

    public DynamoDBUtils(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public Stream<RepaymentItem> scan() {
        try (var dbClient = getDbClient()) {
            var enhancedDbClient = getDbEnhancedClient(dbClient);
            return enhancedDbClient.table(TABLE_REPAYMENT, TABLE_SCHEMA_REPAYMENT).scan().items().stream();
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            return Stream.empty();
        }
    }

    public void getItem(String tableName, String key, String keyVal) {
        try (var dbAsyncClient = getDbAsyncClient()) {
            var req = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(key, AttributeValue.builder().s(keyVal).build()))
                    .build();
            var returnedItem = dbAsyncClient.getItem(req).join().item().values();
            var map = returnedItem.stream().collect(Collectors.toMap(AttributeValue::s, s -> s));
            map.keySet().stream().map(sinKey -> String.format("%s: %s\n", sinKey, map.get(sinKey).toString())).forEach(log::info);
        } catch (DynamoDbException e) {
            log.error("", e);
        }
    }

    public void createTable(String tableName, String key) {
        try (var dbClient = getDbClient();
             var dbWaiter = dbClient.waiter()) {
            var req = CreateTableRequest.builder()
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

            var res = dbClient.createTable(req);
            var tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            // Wait until the Amazon DynamoDB table is created
            var waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().map(Objects::toString).ifPresent(log::info);
            log.info("Table: {}", res.tableDescription().tableName());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void save(Repayment repayment) {
        try (var dbClient = getDbClient()) {
            var dbEnhancedClient = getDbEnhancedClient(dbClient);
            dbEnhancedClient.table(TABLE_REPAYMENT, TABLE_SCHEMA_REPAYMENT).putItem(repayment.dynamoDbItem());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private DynamoDbEnhancedAsyncClient getDbEnhancedAsyncClient(DynamoDbAsyncClient dbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dbAsyncClient)
                .build();
    }

    private DynamoDbEnhancedClient getDbEnhancedClient(DynamoDbClient dbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dbClient)
                .build();
    }

    private DynamoDbAsyncClient getDbAsyncClient() {
        var endpoint = propertiesReader.get("endpoint");
        var region = propertiesReader.get("region");
        var builder = DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create());
        DynamoDbAsyncClient dbAsyncClient;
        if (endpoint.isBlank()) {
            dbAsyncClient = builder.build();
        } else {
            dbAsyncClient = builder
                    .endpointOverride(URI.create(endpoint))
                    .build();
        }
        return dbAsyncClient;
    }

    private DynamoDbClient getDbClient() {
        var endpoint = propertiesReader.get("endpoint");
        var region = propertiesReader.get("region");
        var builder = DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create());
        DynamoDbClient dbClient;
        if (endpoint.isBlank()) {
            dbClient = builder.build();
        } else {
            dbClient = builder
                    .endpointOverride(URI.create(endpoint))
                    .build();
        }
        return dbClient;
    }
}
