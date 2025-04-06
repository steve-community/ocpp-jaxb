package de.rwth.idsg.ocpp.jaxb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

import static de.rwth.idsg.ocpp.DateTimeUtils.toOffsetDateTime;

class JavaDateTimeConverterTest {

    // -------------------------------------------------------------------------
    // Marshal
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("provideZoneIds")
    void testMarshalNullInput(ZoneId zoneId) {
        JavaDateTimeConverter converterUtc = new JavaDateTimeConverter(zoneId, true);
        String valUtc = converterUtc.marshal(null);
        Assertions.assertNull(valUtc);
        JavaDateTimeConverter converter = new JavaDateTimeConverter(zoneId, false);
        String val = converter.marshal(null);
        Assertions.assertNull(val);
    }

    @ParameterizedTest
    @MethodSource("provideValidMarshallingInput")
    void testMarshalUtcValidInput(ZoneId zoneId, OffsetDateTime val, String expected, boolean marshallToUtc) {
        JavaDateTimeConverter converter = new JavaDateTimeConverter(zoneId, marshallToUtc);
        String output = converter.marshal(val);
        Assertions.assertEquals(expected, output);
    }

    private static Stream<Arguments> provideValidMarshallingInput() {
        Stream<Arguments> marchallUtcInputs = Stream.of( //
                Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z", true),
                Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z", true),
                Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z", true),
                Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z", true),
                Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z", true),
                Arguments.of("2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z", true),
                Arguments.of("2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z", true),
                Arguments.of("-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z", true)
                ).flatMap(
                input -> ZONE_IDS.stream()
                        .map(tz -> Arguments.of( //
                                tz, //
                                toOffsetDateTime((String) input.get()[0], ZoneId.of("UTC")), // val
                                input.get()[1], // expected
                                input.get()[2] // marshallToUtc
                        )));
        Stream<Arguments> marchallTzInputs =  Stream.of( //
                Arguments.of("UTC", "2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z", false),
                Arguments.of("Europe/Berlin", "2022-06-30T01:20:52+02:00", "2022-06-30T01:20:52.000+02:00", false),
                Arguments.of("-05:00", "2022-06-30T01:20:52+02:00", "2022-06-29T18:20:52.000-05:00", false),

                Arguments.of("UTC", "2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z", false),
                Arguments.of("Europe/Berlin", "2022-06-30T01:20:52Z", "2022-06-30T03:20:52.000+02:00", false),
                Arguments.of("-05:00", "2022-06-30T01:20:52Z", "2022-06-29T20:20:52.000-05:00", false),

                Arguments.of("UTC", "2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z", false),
                Arguments.of("Europe/Berlin", "2022-06-30T01:20:52+00:00", "2022-06-30T03:20:52.000+02:00", false),
                Arguments.of("-05:00", "2022-06-30T01:20:52+00:00", "2022-06-29T20:20:52.000-05:00", false),

                Arguments.of("UTC", "2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z", false),
                Arguments.of("Europe/Berlin", "2022-06-30T01:20:52.126+05:00", "2022-06-29T22:20:52.126+02:00", false),
                Arguments.of("-05:00", "2022-06-30T01:20:52.126+05:00", "2022-06-29T15:20:52.126-05:00", false),

                Arguments.of("UTC", "2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z", false),
                Arguments.of("Europe/Berlin", "2018-11-13T20:20:39+00:00", "2018-11-13T21:20:39.000+01:00", false),
                Arguments.of("-05:00", "2018-11-13T20:20:39+00:00", "2018-11-13T15:20:39.000-05:00", false),

                Arguments.of("UTC", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z", false),
                Arguments.of("Europe/Berlin", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000+02:00", false),
                Arguments.of("-05:00", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000-05:00", false),

                Arguments.of("UTC", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z", false),
                Arguments.of("Europe/Berlin", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126+02:00", false),
                Arguments.of("-05:00", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126-05:00", false),

                Arguments.of("UTC", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z", false),
                Arguments.of("Europe/Berlin", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000+00:53:28", false),
                Arguments.of("-05:00", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000-05:00", false) //
        ).map(input -> Arguments.of( //
                ZoneId.of((String) input.get()[0]), //
                toOffsetDateTime((String) input.get()[1], ZoneId.of((String) input.get()[0])), // val
                input.get()[2], // expected
                input.get()[3] // marshallToUtc
        ));
        return Stream.concat(marchallUtcInputs, marchallTzInputs);
    }

    // -------------------------------------------------------------------------
    // Unmarshal
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("provideValidUnmarshallingInput")
    void testUnmarshalValid(ZoneId zoneId, String val, ZonedDateTime expected) {
        JavaDateTimeConverter converter = new JavaDateTimeConverter(zoneId, true);
        OffsetDateTime actual = converter.unmarshal(val);
        Assertions.assertEquals(expected.toInstant(), actual.toInstant());
    }

    @ParameterizedTest
    @MethodSource("provideZoneIds")
    void testUnmarshalNullInput(ZoneId zoneId) {
        JavaDateTimeConverter converter = new JavaDateTimeConverter(zoneId, true);
        Assertions.assertDoesNotThrow(() -> converter.unmarshal(null));
    }

    @ParameterizedTest
    @MethodSource("provideValidEmptyUnmarshallingInput")
    void testUnmarshalEmptyInput(ZoneId zoneId, String val) {
        JavaDateTimeConverter converter = new JavaDateTimeConverter(zoneId, true);
        Assertions.assertDoesNotThrow(() -> converter.unmarshal(val));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUnmarshallingInput")
    void testUnmarshalInvalid(ZoneId zoneId, String val) {
        JavaDateTimeConverter converter = new JavaDateTimeConverter(zoneId, true);
        Assertions.assertThrows(DateTimeParseException.class, () -> converter.unmarshal(val));
    }

    private static final List<ZoneId> ZONE_IDS = Stream.of( //
            "UTC", //
            "-05:00", // "EST" is deprecated (like all other short ids)
            "Europe/Berlin", //
            "America/New_York", //
            "Asia/Tokyo", //
            "Australia/Sydney" //
    ).map(ZoneId::of).toList();

    private static Stream<Arguments> provideZoneIds() {
        return ZONE_IDS.stream().map(Arguments::of);
    }

    private static Stream<Arguments> provideValidUnmarshallingInput() {
        Stream<Arguments> validInputs = Stream.of( //
                Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T23:20:52.000Z"),
                Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-30T01:20:52.000+02:00[Europe/Berlin]"),
                Arguments.of("2022-06-30T01:20:52+02:00", "2022-06-29T18:20:52.000-05:00[America/New_York]"),

                Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T01:20:52.000Z"),
                Arguments.of("2022-06-30T01:20:52Z", "2022-06-30T03:20:52.000+02:00[Europe/Berlin]"),
                Arguments.of("2022-06-30T01:20:52Z", "2022-06-29T20:20:52.000-05:00[America/New_York]"),

                Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T01:20:52.000Z"),
                Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-30T03:20:52.000+02:00[Europe/Berlin]"),
                Arguments.of("2022-06-30T01:20:52+00:00", "2022-06-29T20:20:52.000-05:00[America/New_York]"),

                Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T20:20:52.126Z"),
                Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T22:20:52.126+02:00[Europe/Berlin]"),
                Arguments.of("2022-06-30T01:20:52.126+05:00", "2022-06-29T15:20:52.126-05:00[America/New_York]"),

                Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T20:20:39.000Z"),
                Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T21:20:39.000+01:00[Europe/Berlin]"),
                Arguments.of("2018-11-13T20:20:39+00:00", "2018-11-13T15:20:39.000-05:00[America/New_York]")
        ).flatMap(
                input -> ZONE_IDS.stream()
                        .map(tz -> Arguments.of(tz, input.get()[0], ZonedDateTime.parse((String) input.get()[1]))));
        Stream<Arguments> withoutOffsetInputs = Stream.of( //
                tz("UTC", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000Z"),
                tz("Europe/Berlin", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000+02:00[Europe/Berlin]"),
                tz("-05:00", "2022-06-30T01:20:52", "2022-06-30T01:20:52.000-05:00[America/New_York]"),

                tz("UTC", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126Z"),
                tz("Europe/Berlin", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126+02:00[Europe/Berlin]"),
                tz("-05:00", "2022-06-30T01:20:52.126", "2022-06-30T01:20:52.126-05:00[America/New_York]"),

                tz("UTC", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000Z"),
                tz("Europe/Berlin", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000+00:53:28[Europe/Berlin]"),
                tz("-05:00", "-2022-06-30T01:20:52", "-2022-06-30T01:20:52.000-05:00[America/New_York]") //
        );
        return Stream.concat(validInputs, withoutOffsetInputs);
    }

    private static Arguments tz(String zoneId, String input, String expectedZdt) {
        return Arguments.of(ZoneId.of(zoneId), input, ZonedDateTime.parse(expectedZdt));
    }

    private static Stream<Arguments> provideValidEmptyUnmarshallingInput() {
        return Stream.of( //
                "", //
                "      " //
        ).flatMap(input -> ZONE_IDS.stream().map(tz -> Arguments.of(tz, input)));
    }

    private static Stream<Arguments> provideInvalidUnmarshallingInput() {
        return Stream.of( //
                "-1", //
                "10000", // https://github.com/steve-community/steve/issues/1292
                "text", //
                "2022-06-30", // no time
                "2022-06-30T01:20", // seconds are required
                "2022-06-30T25:20:34", // hour out of range
                "22-06-30T25:20:34" // year not YYYY-format
        ).flatMap(input -> ZONE_IDS.stream().map(tz -> Arguments.of(tz, input)));
    }
}
