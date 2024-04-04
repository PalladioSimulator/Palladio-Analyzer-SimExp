package org.palladiosimulator.simexp.dsl.smodel.test.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.presentation.StandardRepresentation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
        EClass eClass = object.eClass();
        result.append(eClass.getName());

        List<String> values = new ArrayList<>();
        for (EAttribute attribute : eClass.getEAllAttributes()) {
            StringBuilder attributeString = new StringBuilder();
            attributeString.append(attribute.getName());
            attributeString.append('=');
            attributeString.append(EcoreUtil.convertToString(attribute.getEAttributeType(), object.eGet(attribute)));
            values.add(attributeString.toString());
        }
        for (EReference reference : eClass.getEAllReferences()) {
            if (!reference.isContainment()) {
                continue;
            }

            StringBuilder referenceString = new StringBuilder();
            referenceString.append(reference.getName());
            referenceString.append("=[");
            if (object.eIsSet(reference)) {
                Object containedObject = object.eGet(reference);
                referenceString.append(toStringOf(containedObject));
            } else {
                referenceString.append("<null>");
            }
            referenceString.append(']');
            values.add(referenceString.toString());
        }

        result.append(": ");
        result.append(StringUtils.join(values, ","));
        return result.toString();
    }
}
