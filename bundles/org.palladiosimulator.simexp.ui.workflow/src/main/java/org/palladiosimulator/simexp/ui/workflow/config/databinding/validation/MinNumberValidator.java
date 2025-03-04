package org.palladiosimulator.simexp.ui.workflow.config.databinding.validation;

import java.util.Comparator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class MinNumberValidator<N extends Number & Comparable<N>> implements IValidator<N> {

    class NumberComparator implements Comparator<N> {

        @Override
        public int compare(N a, N b) throws ClassCastException {
            return a.compareTo(b);
        }
    }

    private final N minValue;
    private final boolean allowNull;
    private final String errorMessage;
    private final NumberComparator nc;

    public MinNumberValidator(String field, N minValue) {
        this(field, minValue, false);
    }

    public MinNumberValidator(String field, N minValue, boolean allowNull) {
        this.minValue = minValue;
        this.allowNull = allowNull;
        this.errorMessage = String.format("%s minimum value is %s%s", field, minValue, allowNull ? " or empty" : "");
        this.nc = new NumberComparator();
    }

    @Override
    public IStatus validate(N value) {
        if (value == null) {
            if (!allowNull) {
                return ValidationStatus.error(errorMessage);
            }
        } else {
            if (nc.compare(minValue, value) > 0) {
                return ValidationStatus.error(errorMessage);
            }
        }
        return ValidationStatus.ok();
    }
}
