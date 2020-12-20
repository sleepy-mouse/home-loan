package cq.playground.home_loan;

public enum RepaymentSchedule {
    FORTNIGHTLY("Fortnightly"), MONTHLY("Monthly");

    private final String description;

    RepaymentSchedule(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    int numberOfRepaymentsPerYear() {
        int numOfRepaymentsPerYear = 0;
        switch (this) {
            case FORTNIGHTLY:
                numOfRepaymentsPerYear = 26;
                break;
            case MONTHLY:
                numOfRepaymentsPerYear = 12;
                break;
        }
        return numOfRepaymentsPerYear;
    }
}
