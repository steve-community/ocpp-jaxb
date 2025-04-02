package de.rwth.idsg.ocpp.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.TimeZone;

/**
 * Java-Time and XSD represent data and time information according to ISO 8601.
 */
public class JavaDateTimeConverter extends XmlAdapter<String, OffsetDateTime> {

    private final ZoneId fallbackZoneId;

    public JavaDateTimeConverter() {
        this(TimeZone.getDefault());
    }

    public JavaDateTimeConverter(TimeZone fallbackTimeZone) {
        this.fallbackZoneId = fallbackTimeZone.toZoneId();
    }

    // Flexible ISO-8601 parser: supports the optional fraction and optional offset.
    // ISO_LOCAL_DATE_TIME cannot be used because SECOND_OF_MINUTE are required here.
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("uuuu-MM-dd'T'HH:mm:ss")

        // Optional: .SSS... (fractional seconds)
        .optionalStart()
        .appendLiteral('.')
        .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false)
        .optionalEnd()

        // Optional: +02:00 or Z (offset)
        .optionalStart()
        .appendOffsetId()
        .optionalEnd()

        .parseStrict()
        .toFormatter();

    // ISO-8601 formatter: outputs timestamps with fixed 3-digits nanosecond precision.
    // ISO_LOCAL_DATE_TIME cannot be used because it does not support the fixed 3-digits nanosecond precision.
    // Note: PARSER and FORMATTER are not interchangeable because PARSER is flexible.
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("uuuu-MM-dd'T'HH:mm:ss.SSS")
        .parseLenient()
        .appendOffsetId()
        .parseStrict()
        .toFormatter();

    @Override
    public OffsetDateTime unmarshal(String v) {
        if (v == null || v.isEmpty()) {
            return null;
        }
        TemporalAccessor parsed = PARSER.parse(v);
        if (!parsed.isSupported(ChronoField.OFFSET_SECONDS)) {
            // Input has no offset → assume fallback zone
            LocalDateTime ldt = LocalDateTime.from(parsed);
            ZoneOffset offset = fallbackZoneId.getRules().getOffset(ldt);
            return ldt.atOffset(offset);
        }
        return OffsetDateTime.from(parsed);
    }

    @Override
    public String marshal(OffsetDateTime v) {
        if (v == null) {
            return null;
        }
        ZoneOffset offset = fallbackZoneId.getRules().getOffset(v.toLocalDateTime());
        return FORMATTER.format(v.withOffsetSameInstant(offset));
    }
}
