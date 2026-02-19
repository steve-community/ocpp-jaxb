package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.bean.BeanSerializerBase;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Slf4j
@RequiredArgsConstructor
public class BeanSerializerWithValidation extends ValueSerializer<Object> {

    private final BeanSerializerBase delegate;
    private final Validator validator;
    private final StrictnessMode strictnessMode;

    @Override
    public void serialize(Object bean, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        validate(bean);
        delegate.serialize(bean, gen, ctxt);
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
