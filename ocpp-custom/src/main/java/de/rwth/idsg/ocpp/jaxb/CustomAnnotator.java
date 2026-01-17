package de.rwth.idsg.ocpp.jaxb;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.jsonschema2pojo.AbstractAnnotator;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

public class CustomAnnotator extends AbstractAnnotator {

    @Override
    public void typeInfo(JDefinedClass clazz, JsonNode schema) {
        super.typeInfo(clazz, schema);

        clazz.annotate(ToString.class);
        clazz.annotate(Getter.class);
        clazz.annotate(Setter.class);
        clazz.annotate(EqualsAndHashCode.class);
    }

    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz,
                              String propertyName,
                              JsonNode propertyNode) {
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
