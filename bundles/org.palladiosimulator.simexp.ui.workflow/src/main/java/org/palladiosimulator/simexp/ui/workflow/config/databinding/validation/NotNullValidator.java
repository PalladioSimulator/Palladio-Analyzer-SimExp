package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class NotNullValidator<T> implements IValidator<T> {
    private final String field;

    public NotNullValidator(String field) {
        this.field = field;
    }

    @Override
    public IStatus validate(T value) {
        if (value == null) {
            return ValidationStatus.error(String.format("%s is empty", field));
        }
        return ValidationStatus.ok();
    }
}
