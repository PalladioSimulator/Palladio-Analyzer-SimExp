package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class NotEmptyValidator implements IValidator<String> {
    private final String field;

    public NotEmptyValidator(String field) {
        this.field = field;
    }

    @Override
    public IStatus validate(String value) {
        if (value.isEmpty()) {
            return ValidationStatus.error(String.format("%s is empty", field));
        }
        return ValidationStatus.ok();
    }
}
