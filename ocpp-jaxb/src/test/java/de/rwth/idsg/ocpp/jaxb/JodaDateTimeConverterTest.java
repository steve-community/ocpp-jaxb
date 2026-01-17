package de.rwth.idsg.ocpp.jaxb;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.TimeZone;
import java.util.stream.Stream;

public class JodaDateTimeConverterTest {

    private final JodaDateTimeConverter converter = new JodaDateTimeConverter();

    // -------------------------------------------------------------------------
    // Marshal
    // -------------------------------------------------------------------------

    @Test
    public void testMarshallNullInput() throws Exception {
        String val = converter.marshal(null);
        Assertions.assertNull(val);
    }

    @ParameterizedTest
    @MethodSource("provideValidInputUTC")
    public void testMarshallValidInputUTC(String val, String expected) throws Exception {
        DateTime input = converter.unmarshal(val);
        String output = converter.marshal(input);
        Assertions.assertEquals(expected, output);
    }

    @ParameterizedTest
    @MethodSource("provideValidInputBerlin")
    public void testMarshallValidInputBerlin(String val, String expected) throws Exception {
        DateTime input = converter.unmarshal(val);
        String output = converter.marshal(input);
        Assertions.assertEquals(expected, output);
    }

    @ParameterizedTest
    @MethodSource("provideValidInputEST")
    public void testMarshallValidInputEST(String val, String expected) throws Exception {
        DateTime input = converter.unmarshal(val);
        String output = converter.marshal(input);
        Assertions.assertEquals(expected, output);
    }

    // -------------------------------------------------------------------------
    // Unmarshal
    // -------------------------------------------------------------------------

    @Test
    public void testUnmarshallNullInput() throws Exception {
        converter.unmarshal(null);
    }

    @Test
    public void testUnmarshallEmptyInput() throws Exception {
        converter.unmarshal("");
    }

    @ParameterizedTest
    @MethodSource("provideValidInputUTC")
    public void testUnmarshalValid(String val) throws Exception {
        converter.unmarshal(val);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    public void testUnmarshalInvalid(String val) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            converter.unmarshal(val);
        });
    }

    /**
     * First argument is used for marshaling only.
     * Both arguments are used for unmarshaling: We use the second as the expected output of formatting.
     */
    private static Stream<Arguments> provideValidInputUTC() {
        setTimeZone("UTC");

        return Stream.of(
            Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z"),
            Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z"),
            Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z"),
            Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z"),
            Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z"),
            Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z"),
            Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z"),
            Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z")
        );
    }

    /**
     * First argument is used for marshaling only.
     * Both arguments are used for unmarshaling: We use the second as the expected output of formatting.
     */
    private static Stream<Arguments> provideValidInputBerlin() {
        setTimeZone("Europe/Berlin");

        return Stream.of(
            Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000+02:00"),
            Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-30T01:20:52.000+02:00"),
            Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T03:20:52.000+02:00"),
            Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T03:20:52.000+02:00"),
            Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126+02:00"),
            Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T22:20:52.126+02:00"),
            Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T21:20:39.000+01:00"),
            Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000+00:53:28")
        );
    }

    /**
     * First argument is used for marshaling only.
     * Both arguments are used for unmarshaling: We use the second as the expected output of formatting.
     */
    private static Stream<Arguments> provideValidInputEST() {
        setTimeZone("EST");

        return Stream.of(
            Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000-05:00"),
            Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T18:20:52.000-05:00"),
            Arguments.of("2022-06-30T01:20:52Z", "2022-06-29T20:20:52.000-05:00"),
            Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-29T20:20:52.000-05:00"),
            Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126-05:00"),
            Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T15:20:52.126-05:00"),
            Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T15:20:39.000-05:00"),
            Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000-05:00")
        );
    }

    private static Stream<Arguments> provideInvalidInput() {
        setTimeZone("UTC"); // just for completeness, it does not matter for this test anyway

        return Stream.of(
            Arguments.of("-1"),
            Arguments.of("10000"), // https://github.com/steve-community/steve/issues/1292
            Arguments.of("text"),
            Arguments.of("2022-06-30"), // no time
            Arguments.of("2022-06-30T01:20"), // seconds are required
            Arguments.of("2022-06-30T25:20:34"), // hour out of range
            Arguments.of("22-06-30T25:20:34") // year not YYYY-format
        );
    }

    private static void setTimeZone(String timeZone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        DateTimeZone.setDefault(DateTimeZone.forID(timeZone));
    }
}
