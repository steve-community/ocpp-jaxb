package de.rwth.idsg.ocpp.jaxb.validation;

import lombok.Builder;
import tools.jackson.databind.module.SimpleModule;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanValidationModule extends SimpleModule {

    @Builder
    private BeanValidationModule(Validator validator, StrictnessMode readingMode, StrictnessMode writingMode) {
        if (readingMode == null && writingMode == null) {
            throw new NullPointerException("readingMode and writingMode are null");
        }

        Validator validatorToUse = validator == null
            ? Validation.buildDefaultValidatorFactory().getValidator()
            : validator;

        if (readingMode != null) {
            setDeserializerModifier(new BeanDeserializerModifierWithValidation(validatorToUse, readingMode));
        }

        if (writingMode != null) {
            setSerializerModifier(new BeanSerializerModifierWithValidation(validatorToUse, writingMode));
        }
    }

    public static BeanValidationModule forReading(Validator validator) {
        return BeanValidationModule.builder()
            .validator(validator)
            .readingMode(StrictnessMode.ThrowError)
            .writingMode(null)
            .build();
    }

    public static BeanValidationModule forWriting(Validator validator) {
        return BeanValidationModule.builder()
            .validator(validator)
            .readingMode(null)
            .writingMode(StrictnessMode.ThrowError)
            .build();
    }

    public static BeanValidationModule forReadingAndWriting(Validator validator) {
        return BeanValidationModule.builder()
            .validator(validator)
            .readingMode(StrictnessMode.ThrowError)
            .writingMode(StrictnessMode.ThrowError)
            .build();
    }

}
