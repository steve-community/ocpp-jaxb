package de.rwth.idsg.ocpp.jaxb;

import de.rwth.idsg.ocpp.DateTimeUtils;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Java-Time and XSD represent data and time information according to ISO 8601.
 */
public class JavaDateTimeConverter extends XmlAdapter<String, OffsetDateTime> {

    private final ZoneId fallbackZoneId;
    private final boolean marchallToUtc;

    public JavaDateTimeConverter() {
        this(ZoneId.systemDefault(), System.getProperty("steve.ocpp.marshall-to-utc", "true").equals("true"));
    }

    public JavaDateTimeConverter(ZoneId fallbackZoneId, boolean marchallToUtc) {
        this.fallbackZoneId = fallbackZoneId;
        this.marchallToUtc = marchallToUtc;
    }

    @Override
    public OffsetDateTime unmarshal(String v) {
        return DateTimeUtils.toOffsetDateTime(v, fallbackZoneId);
    }

    @Override
    public String marshal(OffsetDateTime v) {
        return DateTimeUtils.toString(v, marchallToUtc ? null : fallbackZoneId);
    }
}
