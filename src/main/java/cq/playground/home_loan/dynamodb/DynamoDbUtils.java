package cq.playground.home_loan.dynamodb;

import cq.playground.home_loan.BaseDomain;
import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static software.amazon.awssdk.utils.StringUtils.isBlank;

@Slf4j
public class DynamoDbUtils {
    private final PropertiesReader propertiesReader;

    public DynamoDbUtils(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public <S extends DynamoDbItem<S>> Stream<S> scan(String tableName, TableSchema<S> tableSchema) {
        try (var dbClient = getDbClient()) {
            var enhancedDbClient = getDbEnhancedClient(dbClient);
            return enhancedDbClient.table(tableName, tableSchema).scan().items().stream();
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
            var map = returnedItem.stream().collect(toMap(AttributeValue::s, s -> s));
            map.keySet().stream().map(sinKey -> format("%s: %s\n", sinKey, map.get(sinKey).toString())).forEach(log::info);
        } catch (DynamoDbException e) {
            log.error("", e);
        }
    }

    public void createTable(String tableName, CreateTableRequest createTableRequest) {
        try (var dbClient = getDbClient();
             var dbWaiter = dbClient.waiter()) {
            var res = dbClient.createTable(createTableRequest);
            var tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            var waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().map(Objects::toString).ifPresent(log::info);
            log.info("Table: {}", res.tableDescription().tableName());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public <D extends BaseDomain<S>, S extends DynamoDbItem<S>> void save(D domain) {
        try (var dbClient = getDbClient()) {
            var dbEnhancedClient = getDbEnhancedClient(dbClient);
            var item = domain.dynamoDbItem();
            dbEnhancedClient.table(item.getTableName(), item.getTableSchema()).putItem(item);
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
        if (isBlank(endpoint)) {
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
        if (isBlank(endpoint)) {
            dbClient = builder.build();
        } else {
            dbClient = builder
                    .endpointOverride(URI.create(endpoint))
                    .build();
        }
        return dbClient;
    }
}
