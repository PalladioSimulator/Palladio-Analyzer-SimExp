package org.palladiosimulator.simexp.ui.workflow.config.databinding.conversion;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.conversion.IConverter;

public class ArrayToStringConverter implements IConverter<String[], String> {

    @Override
    public Object getFromType() {
        return String[].class;
    }

    @Override
    public Object getToType() {
        return String.class;
    }

    @Override
    public String convert(String[] fromObject) {
        return StringUtils.join(fromObject, ";");
    }
}
