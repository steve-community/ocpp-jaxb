package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ser.bean.BeanSerializerBase;

import jakarta.validation.Validator;

@RequiredArgsConstructor
public class BeanSerializerModifierWithValidation extends ValueSerializerModifier {

    private final Validator validator;

    @Override
    public ValueSerializer<?> modifySerializer(SerializationConfig config,
                                               BeanDescription.Supplier beanDesc,
                                               ValueSerializer<?> serializer) {
        if (serializer instanceof BeanSerializerBase) {
            return new BeanSerializerWithValidation((BeanSerializerBase) serializer, validator);
        }

        return serializer;
    }
}
