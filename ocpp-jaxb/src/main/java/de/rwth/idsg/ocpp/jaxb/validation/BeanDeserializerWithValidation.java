package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.bean.BeanDeserializerBase;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
@RequiredArgsConstructor
public class BeanDeserializerWithValidation extends ValueDeserializer<Object> {

    private final BeanDeserializerBase delegate;
    private final Validator validator;

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        var instance = delegate.deserialize(p, ctxt);

        var violations = validator.validate(instance);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return instance;
    }
}
