package org.palladiosimulator.simexp.dsl.smodel.test.util;

import java.util.ArrayList;
import java.util.Collection;
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
            String valueAsString = EcoreUtil.convertToString(attribute.getEAttributeType(), object.eGet(attribute));
            attributeString.append(valueAsString);
            values.add(attributeString.toString());
        }
        for (EReference reference : eClass.getEAllReferences()) {
            StringBuilder referenceString = new StringBuilder();
            referenceString.append(reference.getName());
            if (reference.isContainment()) {
                referenceString.append("<>");
            } else {
                referenceString.append("->");
            }
            if (object.eIsSet(reference)) {
                Object containedObject = object.eGet(reference);
                if (containedObject instanceof Collection<?>) {
                    referenceString.append(smartFormat((Collection<?>) containedObject));
                } else {
                    referenceString.append("{");
                    referenceString.append(toStringOf(containedObject));
                    referenceString.append('}');
                }
            } else {
                if (!reference.isMany()) {
                    referenceString.append("<null>");
                }
            }
            values.add(referenceString.toString());
        }

        result.append(": ");
        result.append(StringUtils.join(values, ","));
        return result.toString();
    }
}
