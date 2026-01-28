package de.rwth.idsg.ocpp.jaxb;

import de.rwth.idsg.ocpp.jaxb.validation.BeanValidationModule;
import ocpp.cs._2015._10.StartTransactionRequest;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.joda.JodaModule;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeanSerializerValidationTest {

    private static ObjectMapper mapper;

    @BeforeAll
    public static void setup() {
        mapper = JsonMapper.builder()
            .addModule(new JodaModule())
            .addModule(BeanValidationModule.forWriting(null))
            .build();
    }

    @Test
    public void nullFieldsOcpp12() {
        var exception = assertThrows(ConstraintViolationException.class, () -> mapper.writeValueAsString(new ocpp.cs._2010._08.StartTransactionRequest()));

        var violations = exception.getConstraintViolations();
        checkViolatingNullFields(Set.of("connectorId", "idTag", "timestamp", "meterStart"), violations);
    }

    @Test
    public void nullFieldsOcpp15() {
        var exception = assertThrows(ConstraintViolationException.class, () -> mapper.writeValueAsString(new ocpp.cs._2012._06.StartTransactionRequest()));

        var violations = exception.getConstraintViolations();
        checkViolatingNullFields(Set.of("connectorId", "idTag", "timestamp", "meterStart"), violations);
    }

    @Test
    public void nullFieldsOcpp16() {
        var exception = assertThrows(ConstraintViolationException.class, () -> mapper.writeValueAsString(new ocpp.cs._2015._10.StartTransactionRequest()));

        var violations = exception.getConstraintViolations();
        checkViolatingNullFields(Set.of("connectorId", "idTag", "timestamp", "meterStart"), violations);
    }

    @Test
    public void nullFieldsOcpp16Security() {
        var exception = assertThrows(ConstraintViolationException.class, () -> mapper.writeValueAsString(new ocpp._2022._02.security.SecurityEventNotification()));

        var violations = exception.getConstraintViolations();
        checkViolatingNullFields(Set.of("type", "timestamp"), violations);
    }

    @Test
    public void nullFieldsOcpp2() {
        var exception = assertThrows(ConstraintViolationException.class, () -> mapper.writeValueAsString(new ocpp._2020._03.SecurityEventNotificationRequest()));

        var violations = exception.getConstraintViolations();
        checkViolatingNullFields(Set.of("type", "timestamp"), violations);
    }

    @Test
    public void startTransactionIdTagTooLong() {
        StartTransactionRequest request = new StartTransactionRequest()
            .withConnectorId(1)
            .withMeterStart(0)
            .withTimestamp(DateTime.now())
            .withIdTag("ABCABCABCABCABCABCABCABCABCABCABCABCABCABCABCABCABCABCABCABCABC");

        var exception = assertThrows(ConstraintViolationException.class, () -> mapper.writeValueAsString(request));

        var violations = exception.getConstraintViolations();
        Assertions.assertEquals(1, violations.size());

        ConstraintViolation<?> violation = violations.iterator().next();

        Assertions.assertEquals("idTag", violation.getPropertyPath().toString());
        Assertions.assertEquals("size must be between 0 and 20", violation.getMessage());
    }

    private static void checkViolatingNullFields(Set<String> expected, Set<ConstraintViolation<?>> violations) {
        Assertions.assertEquals(expected.size(), violations.size());

        var violatingFields = violations.stream()
            .map(it -> it.getPropertyPath().toString())
            .collect(Collectors.toSet());

        Assertions.assertEquals(expected, violatingFields);

        var violationReason = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());

        Assertions.assertEquals(violationReason, Set.of("must not be null"));
    }
}
