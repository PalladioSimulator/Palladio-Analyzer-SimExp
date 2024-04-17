package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.net.URL;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class FileURIValidator implements IValidator<String> {
    private final String field;

    public FileURIValidator(String field) {
        this.field = field;
    }

    @Override
    public IStatus validate(String value) {
        try {
            new URL(value);
        } catch (Exception e) {
            return ValidationStatus.error(String.format("%s invalid URI", field));
        }
        return ValidationStatus.ok();
    }

}
