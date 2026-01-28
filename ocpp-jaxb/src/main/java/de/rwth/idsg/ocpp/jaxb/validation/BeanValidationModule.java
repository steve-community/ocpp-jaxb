package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.databind.module.SimpleModule;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanValidationModule extends SimpleModule {

    private BeanValidationModule(Validator validator, boolean forReading, boolean forWriting) {
        Validator validatorToUse = validator == null
            ? Validation.buildDefaultValidatorFactory().getValidator()
            : validator;

        if (forReading) {
            setDeserializerModifier(new BeanDeserializerModifierWithValidation(validatorToUse));
        }

        if (forWriting) {
            setSerializerModifier(new BeanSerializerModifierWithValidation(validatorToUse));
        }
    }

    public static BeanValidationModule forReading(Validator validator) {
        return new BeanValidationModule(validator, true, false);
    }

    public static BeanValidationModule forWriting(Validator validator) {
        return new BeanValidationModule(validator, false, true);
    }

    public static BeanValidationModule forReadingAndWriting(Validator validator) {
        return new BeanValidationModule(validator, true, true);
    }

}
