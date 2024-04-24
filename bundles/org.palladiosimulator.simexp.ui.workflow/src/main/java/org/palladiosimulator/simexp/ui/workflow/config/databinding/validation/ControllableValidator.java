package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ControllableValidator<T> implements IValidator<T> {
    private final IValidator<T> delegate;
    private final Enabled isEnabled;

    public interface Enabled {
        boolean isEnabled();
    }

    public ControllableValidator(IValidator<T> delegate, Enabled isEnabled) {
        this.delegate = delegate;
        this.isEnabled = isEnabled;
    }

    @Override
    public IStatus validate(T value) {
        if (!isEnabled.isEnabled()) {
            return ValidationStatus.ok();
        }
        return delegate.validate(value);
    }
}
