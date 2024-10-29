package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;

public interface IVariableAssigner {
    Object assign(VariableAssignment variableAssignment);
}
