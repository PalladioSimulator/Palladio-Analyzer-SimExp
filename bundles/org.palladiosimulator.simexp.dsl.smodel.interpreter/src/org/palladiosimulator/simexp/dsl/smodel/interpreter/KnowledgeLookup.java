package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EcoreExpression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Runtime;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;

public class KnowledgeLookup {
    private final EObject root;

    public KnowledgeLookup(EObject root) {
        this.root = root;
    }

    public Object getValue(Runtime runtime) {
        EcoreExpression expression = runtime.getExp();
        return getValue(expression, root);
    }

    private Object getValue(EcoreExpression exp, EObject obj) {
        if (exp == null || obj == null) {
            return obj;
        }

        EStructuralFeature feature = obj.eClass()
            .getEStructuralFeature(exp.getName());
        if (feature == null) {
            return null;
        }

        Object value = obj.eGet(feature);

        if (value instanceof EList<?>) {
            return getValue(exp, (EList<?>) value);

        } else if (value instanceof EObject) {
            return getValue(exp.getExp(), (EObject) value);

        } else {
            return value;
        }
    }

    private Object getValue(EcoreExpression exp, EList<?> list) {
        if (exp.isWithIndex()) {
            Object obj = list.get(exp.getIndex());

            if (obj == null) {
                return null;
            }

            return getValue(exp.getExp(), (EObject) obj);
        }

        if (exp.isWithPredicate()) {
            Object literalValue = getValue(exp.getValue());

            for (Object elem : list) {
                EObject eObj = (EObject) elem;
                EStructuralFeature param = eObj.eClass()
                    .getEStructuralFeature(exp.getParam());

                if (param == null) {
                    return null;
                }

                Object paramValue = eObj.eGet(param);

                if (literalValue.equals(paramValue)) {
                    return getValue(exp.getExp(), eObj);
                }
            }
        }

        return null;
    }

    private Object getValue(Literal literal) {
        if (literal instanceof BoolLiteral) {
            return ((BoolLiteral) literal).isTrue();
        }

        if (literal instanceof IntLiteral) {
            return ((IntLiteral) literal).getValue();
        }

        if (literal instanceof FloatLiteral) {
            return ((FloatLiteral) literal).getValue();
        }

        if (literal instanceof StringLiteral) {
            return ((StringLiteral) literal).getValue();
        }

        return null;
    }
}
