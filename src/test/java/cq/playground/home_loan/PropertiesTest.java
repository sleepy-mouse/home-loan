package cq.playground.home_loan;

import cq.playground.home_loan.util.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;

@Slf4j
public class PropertiesTest {
    @Test
    public void readProps() {
        var r = new PropertiesReader();
        var props = r.read();
        props.entrySet().stream().map(Objects::toString).forEach(log::info);
    }
}
