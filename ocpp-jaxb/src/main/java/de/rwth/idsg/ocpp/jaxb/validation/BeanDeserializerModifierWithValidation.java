package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.bean.BeanDeserializerBase;

import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
@RequiredArgsConstructor
public class BeanDeserializerModifierWithValidation extends ValueDeserializerModifier {

    private final Validator validator;

    @Override
    public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                   Supplier beanDescRef,
                                                   ValueDeserializer<?> deserializer) {
        if (deserializer instanceof BeanDeserializerBase) {
            return new BeanDeserializerWithValidation(deserializer, validator);
        }

        return deserializer;
    }
}
