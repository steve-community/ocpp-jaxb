package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.databind.module.SimpleModule;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanValidationModule extends SimpleModule {

    public BeanValidationModule() {
        this(Validation.buildDefaultValidatorFactory().getValidator());
    }

    public BeanValidationModule(Validator validator) {
        setDeserializerModifier(new BeanDeserializerModifierWithValidation(validator));
    }
}
