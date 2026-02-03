package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanDeserializerWithValidation extends DelegatingDeserializer {

    private final Validator validator;

    protected BeanDeserializerWithValidation(ValueDeserializer<?> delegate, Validator validator) {
        super(delegate);
        this.validator = validator;
    }

    @Override
    protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegate) {
        return new BeanDeserializerWithValidation(newDelegate, validator);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        Object instance = super.deserialize(jsonParser, deserializationContext);
        validate(instance);
        return instance;
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Object bean) {
        Object instance = super.deserialize(jsonParser, deserializationContext, bean);
        validate(instance);
        return instance;
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws JacksonException {
        Object instance = super.deserializeWithType(p, ctxt, typeDeserializer);
        validate(instance);
        return instance;
    }

    private <T> void validate(T object) {
        var violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
