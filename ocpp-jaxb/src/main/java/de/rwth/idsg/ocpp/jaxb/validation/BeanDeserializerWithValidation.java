package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.bean.BeanDeserializer;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanDeserializerWithValidation extends BeanDeserializer {

    private final Validator validator;

    public BeanDeserializerWithValidation(BeanDeserializer src, Validator validator) {
        super(src);
        this.validator = validator;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        var instance = super.deserialize(p, ctxt);

        var violations = validator.validate(instance);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return instance;
    }
}
