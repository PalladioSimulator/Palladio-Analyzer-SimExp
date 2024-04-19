package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.util.function.Predicate;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ControllableValidator<T> implements IValidator<T> {
    private final IValidator<T> delegate;
    private final Predicate<ControllableValidator<T>> isEnabled;

    public ControllableValidator(IValidator<T> delegate, Predicate<ControllableValidator<T>> isEnabled) {
        this.delegate = delegate;
        this.isEnabled = isEnabled;
    }

    @Override
    public IStatus validate(T value) {
        if (!isEnabled.test(this)) {
            return ValidationStatus.ok();
        }
        return delegate.validate(value);
    }
}
