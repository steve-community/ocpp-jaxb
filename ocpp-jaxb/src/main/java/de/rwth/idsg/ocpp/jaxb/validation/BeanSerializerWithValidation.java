package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.bean.BeanSerializerBase;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RequiredArgsConstructor
public class BeanSerializerWithValidation extends ValueSerializer<Object> {

    private final BeanSerializerBase delegate;
    private final Validator validator;

    @Override
    public void serialize(Object bean, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        var violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        delegate.serialize(bean, gen, ctxt);
    }

}
