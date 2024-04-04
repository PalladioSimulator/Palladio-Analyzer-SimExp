package org.palladiosimulator.simexp.dsl.smodel.test.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.presentation.StandardRepresentation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class EcoreRepresentation extends StandardRepresentation {
    @Override
    public String toStringOf(Object object) {
        if (object instanceof EObject) {
            return toStringOf((EObject) object);
        }
        return super.toStringOf(object);
    }

    protected String toStringOf(EObject object) {
        StringBuilder result = new StringBuilder();
        result.append(object.eClass()
            .getInstanceTypeName());
        result.append('@');
        result.append(Integer.toHexString(object.hashCode()));

        List<String> attributes = new ArrayList<>();
        for (EAttribute attribute : object.eClass()
            .getEAllAttributes()) {
            StringBuilder attributeString = new StringBuilder();
            attributeString.append(attribute.getName());
            attributeString.append('=');
            attributeString.append(EcoreUtil.convertToString(attribute.getEAttributeType(), object.eGet(attribute)));
            attributes.add(attributeString.toString());
        }
        result.append(": ");
        result.append(StringUtils.join(attributes, ","));
        return result.toString();
    }
}
