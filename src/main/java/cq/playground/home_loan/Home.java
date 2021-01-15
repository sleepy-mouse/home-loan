package cq.playground.home_loan;

import cq.playground.home_loan.dynamodb.HomeItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class Home extends BaseDomain<HomeItem> {
    final String address;
    final BigDecimal price;
    final LocalDate contractDate;
    final LocalDate settlementDate;
    final PropertyEstablishmentType propertyEstablishmentType;

    public Home(String address,
                String price,
                int year,
                int month,
                int dayOfMonth,
                PropertyEstablishmentType propertyEstablishmentType) {
        this.address = address;
        this.price = new BigDecimal(price);
        this.contractDate = LocalDate.of(year, month, dayOfMonth);
        this.settlementDate = this.contractDate;
        this.propertyEstablishmentType = propertyEstablishmentType;
    }

    public static Home build(String address, String price, int year, int month, int dayOfMonth, PropertyEstablishmentType propertyEstablishmentType) {
        return new Home(address, price, year, month, dayOfMonth, propertyEstablishmentType);
    }

    @Override
    public HomeItem dynamoDbItem() {
        var item = new HomeItem();
        item.setAddress(getAddress());
        item.setContractDate(getContractDate());
        item.setSettlementDate(getSettlementDate());
        item.setPropertyEstablishmentType(getPropertyEstablishmentType());
        log.info("{}", this);
        return item;
    }
}
