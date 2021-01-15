package cq.playground.home_loan.dynamodb;

import cq.playground.home_loan.PropertyEstablishmentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Data
@EqualsAndHashCode(callSuper = false)
@DynamoDbBean
public class HomeItem extends DynamoDbItem {
    public static final String TABLE_NAME = "Home";
    public static final String ATTRIBUTE_ADDRESS = "address";
    public static final String ATTRIBUTE_PRICE = "price";
    public static final String ATTRIBUTE_CONTRACT_DATE = "contractDate";
    public static final String ATTRIBUTE_SETTLEMENT_DATE = "settlementDate";
    public static final String ATTRIBUTE_PROPERTY_ESTABLISHMENT_TYPE = "propertyEstablishmentType";
    public static final TableSchema<HomeItem> TABLE_SCHEMA_HOME =
            StaticTableSchema.builder(HomeItem.class).newItemSupplier(HomeItem::new)
                    .addAttribute(String.class, a ->
                            a.name(ATTRIBUTE_ADDRESS)
                                    .getter(HomeItem::getAddress)
                                    .setter(HomeItem::setAddress)
                                    .tags(primaryPartitionKey())
                    )
                    .addAttribute(BigDecimal.class, a ->
                            a.name(ATTRIBUTE_PRICE)
                                    .getter(HomeItem::getPrice)
                                    .setter(HomeItem::setPrice)
                    )
                    .addAttribute(LocalDate.class, a ->
                            a.name(ATTRIBUTE_CONTRACT_DATE)
                                    .getter(HomeItem::getContractDate)
                                    .setter(HomeItem::setContractDate)
                    )
                    .addAttribute(LocalDate.class, a ->
                            a.name(ATTRIBUTE_SETTLEMENT_DATE)
                                    .getter(HomeItem::getSettlementDate)
                                    .setter(HomeItem::setSettlementDate)
                    )
                    .addAttribute(PropertyEstablishmentType.class, a ->
                            a.name(ATTRIBUTE_PROPERTY_ESTABLISHMENT_TYPE)
                                    .getter(HomeItem::getPropertyEstablishmentType)
                                    .setter(HomeItem::setPropertyEstablishmentType)
                    )
                    .build();

    public static final CreateTableRequest CREATE_TABLE_REQUEST = CreateTableRequest.builder()
            .attributeDefinitions(AttributeDefinition.builder()
                    .attributeName(ATTRIBUTE_ADDRESS)
                    .attributeType(ScalarAttributeType.S)
                    .build())
            .keySchema(KeySchemaElement.builder()
                    .attributeName(ATTRIBUTE_ADDRESS)
                    .keyType(KeyType.HASH)
                    .build())
            .provisionedThroughput(ProvisionedThroughput.builder()
                    .readCapacityUnits(10L)
                    .writeCapacityUnits(10L)
                    .build())
            .tableName(TABLE_NAME)
            .build();

    private String address;
    private BigDecimal price;
    private LocalDate contractDate;
    private LocalDate settlementDate;
    private PropertyEstablishmentType propertyEstablishmentType;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = round(price);
    }
}
