package cq.playground.home_loan;

public interface Fee {
}

class GovernmentFee implements Fee {
    enum GovernmentFeeType {
        REGISTRATION_FEE, TITLE_SEARCH_FEE, PRIORITY_NOTICE_FEE
    }
}

class ThirdPartyFee implements Fee {
    enum ThirdPartyFeeType {
        SETTLEMENT_FEE
    }
}

class LenderFee implements Fee {
    enum LenderFeeType {
        APPLICATION_FEE, VALUATION_FEE, MONTHLY_SERVICE_FEE, ANNUAL_FEE
    }
}
