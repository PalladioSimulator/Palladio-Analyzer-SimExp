package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class MinIntegerValidator implements IValidator<Integer> {
    private final String field;
    private final int minValue;

    public MinIntegerValidator(String field, int minValue) {
        this.field = field;
        this.minValue = minValue;
    }

    @Override
    public IStatus validate(Integer value) {
        if ((value == null) || (minValue > value)) {
            return ValidationStatus.error(String.format("%s minimum value is %s", field, minValue));
        }
        return ValidationStatus.ok();
    }
}
