package de.rwth.idsg.ocpp.jaxb;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

import static de.rwth.idsg.ocpp.jaxb.Utils.FORMATTER;
import static de.rwth.idsg.ocpp.jaxb.Utils.isNullOrEmpty;

public class JodaDateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getText();
        if (isNullOrEmpty(value)) {
            return null;
        }
        return FORMATTER.parseDateTime(value);
    }
}
