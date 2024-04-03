package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;

public class KnowledgeLookup {
    private final EObject root;

    public KnowledgeLookup(EObject root) {
        this.root = root;
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
