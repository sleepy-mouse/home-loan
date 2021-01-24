package cq.playground.home_loan.dynamodb;

import cq.playground.home_loan.RepaymentSchedule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Data
@EqualsAndHashCode(callSuper = false)
@DynamoDbBean
public class RepaymentItem extends DynamoDbItem<RepaymentItem> {
    public static final String TABLE_REPAYMENT = "Repayment";
    public static final String ATTRIBUTE_REPAYMENT_ID = "repaymentId";
    public static final String ATTRIBUTE_LOAN_BALANCE = "loanBalance";
    public static final String ATTRIBUTE_REPAYMENT_AMOUNT = "repaymentAmount";
    public static final String ATTRIBUTE_INTEREST_PAID = "interestPaid";
    public static final String ATTRIBUTE_PRINCIPAL_PAID = "principalPaid";
    public static final String ATTRIBUTE_SCHEDULE = "schedule";
    public static final String ATTRIBUTE_PAID_AT = "paidAt";

    public static final TableSchema<RepaymentItem> TABLE_SCHEMA_REPAYMENT =
            StaticTableSchema.builder(RepaymentItem.class).newItemSupplier(RepaymentItem::new)
                    .addAttribute(String.class, a ->
                            a.name(ATTRIBUTE_REPAYMENT_ID)
                                    .getter(RepaymentItem::getRepaymentItemId)
                                    .setter(RepaymentItem::setRepaymentItemId)
                                    .tags(primaryPartitionKey())
                    )
                    .addAttribute(BigDecimal.class, a ->
                            a.name(ATTRIBUTE_LOAN_BALANCE)
                                    .getter(RepaymentItem::getLoanBalance)
                                    .setter(RepaymentItem::setLoanBalance)
                    )
                    .addAttribute(BigDecimal.class, a ->
                            a.name(ATTRIBUTE_REPAYMENT_AMOUNT)
                                    .getter(RepaymentItem::getRepaymentAmount)
                                    .setter(RepaymentItem::setRepaymentAmount)
                    )
                    .addAttribute(BigDecimal.class, a ->
                            a.name(ATTRIBUTE_INTEREST_PAID)
                                    .getter(RepaymentItem::getInterestPaid)
                                    .setter(RepaymentItem::setInterestPaid)
                    )
                    .addAttribute(BigDecimal.class, a ->
                            a.name(ATTRIBUTE_PRINCIPAL_PAID)
                                    .getter(RepaymentItem::getPrincipalPaid)
                                    .setter(RepaymentItem::setPrincipalPaid)
                    )
                    .addAttribute(RepaymentSchedule.class, a ->
                            a.name(ATTRIBUTE_SCHEDULE)
                                    .getter(RepaymentItem::getSchedule)
                                    .setter(RepaymentItem::setSchedule)
                    )
                    .addAttribute(LocalDateTime.class, a ->
                            a.name(ATTRIBUTE_PAID_AT)
                                    .getter(RepaymentItem::getPaidAt)
                                    .setter(RepaymentItem::setPaidAt)
                    )
                    .build();

    public static final CreateTableRequest CREATE_TABLE_REQUEST = CreateTableRequest.builder()
            .attributeDefinitions(AttributeDefinition.builder()
                    .attributeName(ATTRIBUTE_REPAYMENT_ID)
                    .attributeType(ScalarAttributeType.S)
                    .build())
            .keySchema(KeySchemaElement.builder()
                    .attributeName(ATTRIBUTE_REPAYMENT_ID)
                    .keyType(KeyType.HASH)
                    .build())
            .provisionedThroughput(ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build())
            .tableName(TABLE_REPAYMENT)
            .build();

    private String repaymentItemId;
    private BigDecimal loanBalance;
    private BigDecimal repaymentAmount;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;
    private RepaymentSchedule schedule;
    private LocalDateTime paidAt;

    @DynamoDbPartitionKey
    public String getRepaymentItemId() {
        return repaymentItemId;
    }

    @DynamoDbAttribute(ATTRIBUTE_LOAN_BALANCE)
    public BigDecimal getLoanBalance() {
        return loanBalance;
    }

    public void setLoanBalance(BigDecimal loanBalance) {
        this.loanBalance = round(loanBalance);
    }

    @DynamoDbAttribute(ATTRIBUTE_REPAYMENT_AMOUNT)
    public BigDecimal getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
        this.repaymentAmount = round(repaymentAmount);
    }

    @DynamoDbAttribute(ATTRIBUTE_INTEREST_PAID)
    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = round(interestPaid);
    }

    @DynamoDbAttribute(ATTRIBUTE_PRINCIPAL_PAID)
    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = round(principalPaid);
    }

    @DynamoDbAttribute(ATTRIBUTE_SCHEDULE)
    public RepaymentSchedule getSchedule() {
        return schedule;
    }

    @DynamoDbAttribute(ATTRIBUTE_PAID_AT)
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    @Override
    public String getTableName() {
        return TABLE_REPAYMENT;
    }

    @Override
    public TableSchema<RepaymentItem> getTableSchema() {
        return TABLE_SCHEMA_REPAYMENT;
    }
}
