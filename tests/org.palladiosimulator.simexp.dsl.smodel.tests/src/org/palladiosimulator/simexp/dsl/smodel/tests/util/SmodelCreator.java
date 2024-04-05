package org.palladiosimulator.simexp.dsl.smodel.tests.util;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;

public class SmodelCreator {
    public SetBounds createSetBoundsBool(Literal... values) {
        SetBounds bounds = SmodelFactory.eINSTANCE.createSetBounds();
        for (Literal value : values) {
            bounds.getValues()
                .add(value);
        }
        return bounds;
    }

    public Optimizable createOptimizable(String name, DataType type, Bounds bounds) {
        Optimizable optimizable = SmodelFactory.eINSTANCE.createOptimizable();
        optimizable.setName(name);
        optimizable.setDataType(type);
        optimizable.setValues(bounds);
        return optimizable;
    }

    public Parameter createParameter(String name, DataType type) {
        Parameter parameter = SmodelFactory.eINSTANCE.createParameter();
        parameter.setName(name);
        parameter.setDataType(type);
        return parameter;
    }

    public Action createAction(String name) {
        Action action = SmodelFactory.eINSTANCE.createAction();
        action.setName(name);
        ActionArguments actionArguments = SmodelFactory.eINSTANCE.createActionArguments();
        action.setArguments(actionArguments);
        return action;
    }

    public IntLiteral createIntLiteral(int value) {
        IntLiteral literal = SmodelFactory.eINSTANCE.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

    public DoubleLiteral createDoubleLiteral(double value) {
        DoubleLiteral literal = SmodelFactory.eINSTANCE.createDoubleLiteral();
        literal.setValue(value);
        return literal;
    }
}
