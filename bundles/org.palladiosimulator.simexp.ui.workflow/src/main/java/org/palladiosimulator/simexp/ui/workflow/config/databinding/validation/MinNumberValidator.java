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

    private final String field;
    private final N minValue;
    private final NumberComparator nc;

    public MinNumberValidator(String field, N minValue) {
        this.field = field;
        this.minValue = minValue;
        this.nc = new NumberComparator();
    }

    @Override
    public IStatus validate(N value) {
        if ((value == null) || (nc.compare(minValue, value) > 0)) {
            return ValidationStatus.error(String.format("%s minimum value is %s", field, minValue));
        }
        return ValidationStatus.ok();
    }
}
