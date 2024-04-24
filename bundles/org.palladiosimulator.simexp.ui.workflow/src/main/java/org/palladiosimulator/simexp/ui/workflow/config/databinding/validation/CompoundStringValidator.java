package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class CompoundStringValidator implements IValidator<String> {
    private final List<IValidator<String>> validators;

    public CompoundStringValidator(List<IValidator<String>> validators) {
        this.validators = validators;
    }

    @Override
    public IStatus validate(String value) {
        IStatus result = ValidationStatus.ok();
        for (IValidator<String> validator : validators) {
            IStatus status = validator.validate(value);
            if (status.getSeverity() > result.getSeverity()) {
                result = status;
            }
        }
        return result;
    }
}
