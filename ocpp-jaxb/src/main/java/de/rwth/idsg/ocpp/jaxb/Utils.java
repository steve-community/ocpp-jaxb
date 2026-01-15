package de.rwth.idsg.ocpp.jaxb;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import static org.joda.time.format.ISODateTimeFormat.date;

public class Utils {

    public static final DateTimeFormatter FORMATTER = dateTimeParser();

    /**
     * Because I did not want to include Guava or similar only for this.
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * A custom DateTimeFormatter that follows the strictness and flexibility of XSD:dateTime (ISO 8601).
     * This exact composition (with optional fields) is not present under {@link org.joda.time.format.ISODateTimeFormat}.
     */
    private static DateTimeFormatter dateTimeParser() {
        return new DateTimeFormatterBuilder()
            .append(date())
            .appendLiteral('T')
            .append(hourElement())
            .append(minuteElement())
            .append(secondElement())
            .appendOptional(fractionElement().getParser())
            .appendOptional(offsetElement().getParser())
            .toFormatter();
    }

    // -------------------------------------------------------------------------
    // Copy-paste from "private" methods in ISODateTimeFormat
    // -------------------------------------------------------------------------

    private static DateTimeFormatter hourElement() {
        return new DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .toFormatter();
    }

    private static DateTimeFormatter minuteElement() {
        return new DateTimeFormatterBuilder()
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .toFormatter();
    }

    private static DateTimeFormatter secondElement() {
        return new DateTimeFormatterBuilder()
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toFormatter();
    }

    private static DateTimeFormatter fractionElement() {
        return new DateTimeFormatterBuilder()
            .appendLiteral('.')
            // Support parsing up to nanosecond precision even though
            // those extra digits will be dropped.
            .appendFractionOfSecond(3, 9)
            .toFormatter();
    }

    private static DateTimeFormatter offsetElement() {
        return new DateTimeFormatterBuilder()
            .appendTimeZoneOffset("Z", true, 2, 4)
            .toFormatter();
    }
}
