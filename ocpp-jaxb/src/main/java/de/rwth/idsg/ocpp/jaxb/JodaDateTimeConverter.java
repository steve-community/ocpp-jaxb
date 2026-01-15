package de.rwth.idsg.ocpp.jaxb;

import org.joda.time.DateTime;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import static de.rwth.idsg.ocpp.jaxb.Utils.FORMATTER;
import static de.rwth.idsg.ocpp.jaxb.Utils.isNullOrEmpty;

/**
 * Joda-Time and XSD represent data and time information according to ISO 8601.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.10.2014
 */
public class JodaDateTimeConverter extends XmlAdapter<String, DateTime> {

    @Override
    public DateTime unmarshal(String v) throws Exception {
        if (isNullOrEmpty(v)) {
            return null;
        } else {
            return FORMATTER.parseDateTime(v);
        }
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return v.toString();
        }
    }
}
