package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.util.Comparator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class MaxNumberValidator<N extends Number & Comparable<N>> implements IValidator<N> {

    class NumberComparator implements Comparator<N> {

        @Override
        public int compare(N a, N b) throws ClassCastException {
            return a.compareTo(b);
        }
    }

    private final N maxValue;
    private final boolean allowNull;
    private final String errorMessage;
    private final NumberComparator nc;

    public MaxNumberValidator(String field, N maxValue) {
        this(field, maxValue, false);
    }

    public MaxNumberValidator(String field, N maxValue, boolean allowNull) {
        this.maxValue = maxValue;
        this.allowNull = allowNull;
        this.errorMessage = String.format("%s maximum value is %s%s", field, maxValue, allowNull ? " or empty" : "");
        this.nc = new NumberComparator();
    }

    @Override
    public IStatus validate(N value) {
        if (value == null) {
            if (!allowNull) {
                return ValidationStatus.error(errorMessage);
            }
        } else {
            if (nc.compare(maxValue, value) < 0) {
                return ValidationStatus.error(errorMessage);
            }
        }
        return ValidationStatus.ok();
    }
}
