package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ExtensionValidator implements IValidator<String> {
    private final String field;
    private final String extension;
    private final PathMatcher matcher;

    public ExtensionValidator(String field, String extension) {
        this.field = field;
        this.extension = extension;
        matcher = FileSystems.getDefault()
            .getPathMatcher(String.format("glob:%s", extension));
    }

    @Override
    public IStatus validate(String value) {
        try {
            URL url = new URL(value);
            Path path = Paths.get(url.getPath());
            if (matcher.matches(path.getFileName())) {
                return ValidationStatus.ok();
            }
        } catch (Exception e) {
        }
        return ValidationStatus.error(String.format("%s invalid extension (%s)", field, extension));
    }

}
