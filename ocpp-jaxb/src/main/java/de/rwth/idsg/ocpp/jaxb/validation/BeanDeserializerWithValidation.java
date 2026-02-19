package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BeanDeserializerWithValidation extends DelegatingDeserializer {

    private final Validator validator;
    private final StrictnessMode strictnessMode;

    protected BeanDeserializerWithValidation(ValueDeserializer<?> delegate, Validator validator,
                                             StrictnessMode strictnessMode) {
        super(delegate);
        this.validator = validator;
        this.strictnessMode = strictnessMode;
    }

    @Override
    protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegate) {
        return new BeanDeserializerWithValidation(newDelegate, validator, strictnessMode);
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
        if (violations.isEmpty()) {
            return;
        }

        var exception = new ConstraintViolationException(violations);
        switch (strictnessMode) {
            case LogWarning -> log.warn("There are constraint violations", exception);
            case ThrowError -> throw exception;
        }
    }
}
