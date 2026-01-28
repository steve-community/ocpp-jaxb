package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.BeanSerializer;
import tools.jackson.databind.ser.bean.BeanSerializerBase;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

public class BeanSerializerWithValidation extends BeanSerializer {

    private final Validator validator;

    public BeanSerializerWithValidation(BeanSerializerBase src, Validator validator) {
        super(src);
        this.validator = validator;
    }

    @Override
    public void serialize(Object bean, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        var violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        super.serialize(bean, gen, provider);
    }
}
