package cq.playground.home_loan;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static cq.playground.home_loan.PropertyEstablishmentType.ESTABLISHED_HOME;

public class BaseTest {
    public static final Home HOME_35_ROYCROFT_AVENUE = Home.build("35 Roycroft Avenue, Mill Park, VIC 3082", "780000", 2021, 6, 1, ESTABLISHED_HOME);
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void delimiter(int counter, RepaymentSchedule repaymentSchedule) {
        System.out.printf("%3d *************************************************************************************************** %s%n", counter, repaymentSchedule.getDescription());
    }

    public static void delimiter() {
        System.out.printf("*************************************************************************************************** %n");
    }
}
