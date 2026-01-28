package de.rwth.idsg.ocpp.jaxb.validation;

import tools.jackson.databind.module.SimpleModule;

/**
 * https://www.baeldung.com/java-object-validation-deserialization
 */
public class BeanValidationModule extends SimpleModule {

    public BeanValidationModule() {
        super();
        setDeserializerModifier(new BeanDeserializerModifierWithValidation());
    }
}
