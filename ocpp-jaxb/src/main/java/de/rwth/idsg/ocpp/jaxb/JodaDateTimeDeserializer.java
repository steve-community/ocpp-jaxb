package de.rwth.idsg.ocpp.jaxb;

import org.joda.time.DateTime;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import static de.rwth.idsg.ocpp.jaxb.Utils.FORMATTER;
import static de.rwth.idsg.ocpp.jaxb.Utils.isNullOrEmpty;

public class JodaDateTimeDeserializer extends ValueDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String value = p.getString();
        if (isNullOrEmpty(value)) {
            return null;
        }
        return FORMATTER.parseDateTime(value);
    }
}
