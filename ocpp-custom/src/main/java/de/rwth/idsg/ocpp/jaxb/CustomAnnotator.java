package de.rwth.idsg.ocpp.jaxb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.joda.time.DateTime;
import org.jsonschema2pojo.AbstractAnnotator;

public class CustomAnnotator extends AbstractAnnotator {

    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz,
                              String propertyName,
                              com.fasterxml.jackson.databind.JsonNode propertyNode) {
        super.propertyField(field, clazz, propertyName, propertyNode);

        // Add custom converter annotations to all DateTime fields
        if (field.type().fullName().equals(DateTime.class.getName())) {
            // Add @JsonSerialize annotation
            field.annotate(JsonSerialize.class)
                .param("using", clazz.owner().ref("de.rwth.idsg.ocpp.jaxb.JodaDateTimeSerializer").dotclass());

            // Add @JsonDeserialize annotation
            field.annotate(JsonDeserialize.class)
                .param("using", clazz.owner().ref("de.rwth.idsg.ocpp.jaxb.JodaDateTimeDeserializer").dotclass());
        }
    }
}
