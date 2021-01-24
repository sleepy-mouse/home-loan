package cq.playground.home_loan.dynamodb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.math.BigDecimal;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

@Data
@EqualsAndHashCode(callSuper = false)
@DynamoDbBean
public class PriceDutyItem extends DynamoDbItem<PriceDutyItem> {
    public static final String ATTRIBUTE_PRICE = "price";
    public static final String ATTRIBUTE_DUTY = "duty";
    public static final TableSchema<PriceDutyItem> TABLE_SCHEMA_PRICE_DUTY =
            StaticTableSchema.builder(PriceDutyItem.class).newItemSupplier(PriceDutyItem::new)
                    .addAttribute(Integer.class, a ->
                            a.name(ATTRIBUTE_PRICE)
                                    .getter(PriceDutyItem::getPrice)
                                    .setter(PriceDutyItem::setPrice)
                                    .tags(primaryPartitionKey())
                    )
                    .addAttribute(BigDecimal.class, a ->
                            a.name(ATTRIBUTE_DUTY)
                                    .getter(PriceDutyItem::getDuty)
                                    .setter(PriceDutyItem::setDuty)
                                    .tags(primarySortKey())
                    )
                    .build();
    public static final String TABLE_PRICE_DUTY = "PriceDuty";
    public static final CreateTableRequest CREATE_TABLE_REQUEST = CreateTableRequest.builder()
            .attributeDefinitions(
                    AttributeDefinition.builder()
                            .attributeName(ATTRIBUTE_PRICE)
                            .attributeType(ScalarAttributeType.N)
                            .build(),
                    AttributeDefinition.builder()
                            .attributeName(ATTRIBUTE_DUTY)
                            .attributeType(ScalarAttributeType.N)
                            .build())
            .keySchema(
                    KeySchemaElement.builder()
                            .attributeName(ATTRIBUTE_PRICE)
                            .keyType(KeyType.HASH)
                            .build(),
                    KeySchemaElement.builder()
                            .attributeName(ATTRIBUTE_DUTY)
                            .keyType(KeyType.RANGE)
                            .build())
            .provisionedThroughput(ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build())
            .tableName(TABLE_PRICE_DUTY)
            .build();

    private Integer price;
    private BigDecimal duty;

    @DynamoDbPartitionKey
    public Integer getPrice() {
        return price;
    }

    @DynamoDbSortKey
    public BigDecimal getDuty() {
        return duty;
    }

    @Override
    public String getTableName() {
        return TABLE_PRICE_DUTY;
    }

    @Override
    public TableSchema<PriceDutyItem> getTableSchema() {
        return TABLE_SCHEMA_PRICE_DUTY;
    }
}
