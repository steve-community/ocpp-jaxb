package de.rwth.idsg.ocpp.jaxb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

class JavaDateTimeConverterTest {

    // -------------------------------------------------------------------------
    // Marshal
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("provideTimeZoneIds")
    void testMarshallNullInput(String tzId) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(timeZone);
        String val = converter.marshal(null);
        Assertions.assertNull(val);
    }

    @ParameterizedTest
    @MethodSource("provideValidInput")
    void testMarshallValidInput(String val, String expected, String tzId) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(timeZone);
        OffsetDateTime input = converter.unmarshal(val);
        String output = converter.marshal(input);
        Assertions.assertEquals(expected, output);
    }

    // -------------------------------------------------------------------------
    // Unmarshal
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("provideTimeZoneIds")
    void testUnmarshallNullInput(String tzId) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(timeZone);
        Assertions.assertDoesNotThrow(() -> {
            converter.unmarshal(null);
        });
    }

    @ParameterizedTest
    @MethodSource("provideTimeZoneIds")
    void testUnmarshallEmptyInput(String tzId) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(timeZone);
        Assertions.assertDoesNotThrow(() -> {
            converter.unmarshal("");
        });
    }

    @ParameterizedTest
    @MethodSource("provideValidInput")
    void testUnmarshalValid(String val, String expected, String tzId) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(timeZone);
        Assertions.assertDoesNotThrow(() -> {
            converter.unmarshal(val);
        });
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUnmarshallingInput")
    void testUnmarshalInvalid(String val, String tzId) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(timeZone);
        Assertions.assertThrows(DateTimeParseException.class, () -> {
            converter.unmarshal(val);
        });
    }

    private static final List<String> TIME_ZONES = List.of(
        "UTC",
        "EST",
        "Europe/Berlin",
        "America/New_York",
        "Asia/Tokyo",
        "Australia/Sydney"
    );

    private static Stream<Arguments> provideTimeZoneIds() {
        return TIME_ZONES.stream().map(Arguments::of);
    }

    /**
     * Marshaling: All arguments are used (value, expected, TZ id)
     * Unmarshalling: Only the first argument is used (value)
     */
    private static Stream<Arguments> provideValidInput() {
        return Stream.of(
            Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z", "UTC"),
            Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000+02:00", "Europe/Berlin"),
            Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000-05:00", "EST"),

            Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z", "UTC"),
            Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-30T01:20:52.000+02:00", "Europe/Berlin"),
            Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T18:20:52.000-05:00", "EST"),

            Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z", "UTC"),
            Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T03:20:52.000+02:00", "Europe/Berlin"),
            Arguments.of("2022-06-30T01:20:52Z", "2022-06-29T20:20:52.000-05:00", "EST"),

            Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z", "UTC"),
            Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T03:20:52.000+02:00", "Europe/Berlin"),
            Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-29T20:20:52.000-05:00", "EST"),

            Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z", "UTC"),
            Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126+02:00", "Europe/Berlin"),
            Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126-05:00", "EST"),

            Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z", "UTC"),
            Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T22:20:52.126+02:00", "Europe/Berlin"),
            Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T15:20:52.126-05:00", "EST"),

            Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z", "UTC"),
            Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T21:20:39.000+01:00", "Europe/Berlin"),
            Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T15:20:39.000-05:00", "EST"),

            Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z", "UTC"),
            Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000+00:53:28", "Europe/Berlin"),
            Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000-05:00", "EST")
        );
    }

    private static Stream<Arguments> provideInvalidUnmarshallingInput() {
        return Stream.of(
            "-1",
            "10000", // https://github.com/steve-community/steve/issues/1292
            "text",
            "2022-06-30", // no time
            "2022-06-30T01:20", // seconds are required
            "2022-06-30T25:20:34", // hour out of range
            "22-06-30T25:20:34" // year not YYYY-format
        ).flatMap(input -> TIME_ZONES.stream().map(tz -> Arguments.of(input, tz)));
    }
}
