package de.rwth.idsg.ocpp.jaxb;

import org.joda.time.DateTime;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class JodaDateTimeSerializer extends ValueSerializer<DateTime> {

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.toString());
        }
    }
}
