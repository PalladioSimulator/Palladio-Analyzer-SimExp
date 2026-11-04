package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class RangeNumberValidator<N extends Number & Comparable<N>> implements IValidator<N> {
    private final IValidator<N> minValidator;
    private final IValidator<N> maxValidator;

    public RangeNumberValidator(String field, N minValue, N maxValue) {
        this(field, minValue, maxValue, false);
    }

    public RangeNumberValidator(String field, N minValue, N maxValue, boolean allowNull) {
        this.minValidator = new MinNumberValidator<>(field, minValue, allowNull);
        this.maxValidator = new MaxNumberValidator<>(field, maxValue, allowNull);
    }

    @Override
    public IStatus validate(N value) {
        IStatus status = minValidator.validate(value);
        if (!status.isOK()) {
            return status;
        }
        status = maxValidator.validate(value);
        if (!status.isOK()) {
            return status;
        }
        return ValidationStatus.ok();
    }
}
