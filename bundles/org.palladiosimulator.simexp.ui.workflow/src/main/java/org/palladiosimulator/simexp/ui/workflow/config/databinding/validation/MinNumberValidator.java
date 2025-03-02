package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.util.Comparator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class MinNumberValidator<N extends Number & Comparable<N>> implements IValidator<N> {
    private final String field;
    private final N minValue;

    public MinNumberValidator(String field, N minValue) {
        this.field = field;
        this.minValue = minValue;
    }

    @Override
    public IStatus validate(N value) {
        class NumberComparator implements Comparator<N> {

            @Override
            public int compare(N a, N b) throws ClassCastException {
                return a.compareTo(b);
            }
        }

        NumberComparator nc = new NumberComparator();
        if ((value == null) || (nc.compare(minValue, value) > 0)) {
            return ValidationStatus.error(String.format("%s minimum value is %s", field, minValue));
        }
        return ValidationStatus.ok();
    }
}
