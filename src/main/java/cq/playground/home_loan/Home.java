package cq.playground.home_loan;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
class Home {
    final BigDecimal price;
    final LocalDate contractDate;
    final LocalDate settlementDate;
    final PropertyEstablishmentType propertyEstablishmentType;

    public Home(String price, int year, int month, int dayOfMonth, PropertyEstablishmentType propertyEstablishmentType) {
        this.price = new BigDecimal(price);
        this.contractDate = LocalDate.of(year, month, dayOfMonth);
        this.settlementDate = this.contractDate;
        this.propertyEstablishmentType = propertyEstablishmentType;
    }
}
