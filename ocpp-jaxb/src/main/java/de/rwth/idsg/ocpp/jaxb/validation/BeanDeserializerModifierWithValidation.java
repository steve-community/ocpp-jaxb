package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.bean.BeanDeserializer;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanDeserializerModifierWithValidation extends ValueDeserializerModifier {

    @Override
    public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                   Supplier beanDescRef,
                                                   ValueDeserializer<?> deserializer) {
        if (deserializer instanceof BeanDeserializer) {
            return new BeanDeserializerWithValidation((BeanDeserializer) deserializer);
        }

        return deserializer;
    }
}
