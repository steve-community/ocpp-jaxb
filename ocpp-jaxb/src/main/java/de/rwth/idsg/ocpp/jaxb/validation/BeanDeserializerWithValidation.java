package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.bean.BeanDeserializerBase;
import tools.jackson.databind.deser.std.DelegatingDeserializer;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanDeserializerWithValidation extends DelegatingDeserializer {

    public BeanDeserializerWithValidation(BeanDeserializerBase d, Validator validator) {
        super(create(d, validator));
    }

    @Override
    protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegatee) {
        return newDelegatee;
    }

    private static ValueDeserializer<?> create(ValueDeserializer<?> delegate, Validator validator) {
        return new ValueDeserializer<>() {

            @Override
            public Class<?> handledType() {
                return delegate.handledType(); // otherwise NPE in constructor of DelegatingDeserializer
            }

            @Override
            public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
                var instance = delegate.deserialize(p, ctxt);

                var violations = validator.validate(instance);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }

                return instance;
            }
        };
    }
}
