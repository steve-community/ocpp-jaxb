package de.rwth.idsg.ocpp.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.12.2018
 */
public class OffsetDateTimeConverter extends XmlAdapter<String, OffsetDateTime> {

    private static ZoneId fallbackZoneId = null;

    @Override
    public String marshal(OffsetDateTime v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(v);
        }
    }

    @Override
    public OffsetDateTime unmarshal(String v) throws Exception {
        if (isNullOrEmpty(v)) {
            return null;
        } else {
            try {
                return OffsetDateTime.parse(v, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (Exception e1) {
                try {
                    // try to handle offsetless xsd:dateTime
                    LocalDateTime localDateTime = LocalDateTime.parse(v, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    // depending on zone id the offset might change during the year.
                    // this is why we re-calculate it each time.
                    return localDateTime.atZone(getFallbackZoneId()).toOffsetDateTime();
                } catch (Exception e2) {
                    throw e1;
                }
            }
        }
    }

    public static void setFallbackZoneId(ZoneId zoneId) {
        fallbackZoneId = zoneId;
    }

    public static ZoneId getFallbackZoneId() {
        if (fallbackZoneId == null) {
            return ZoneId.systemDefault();
        } else {
            return fallbackZoneId;
        }
    }

    /**
     * Because I did not want to include Guava or similar only for this.
     */
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
