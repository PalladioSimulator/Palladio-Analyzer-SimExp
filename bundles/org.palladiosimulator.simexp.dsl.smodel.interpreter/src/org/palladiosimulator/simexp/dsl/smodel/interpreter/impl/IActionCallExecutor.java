package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;

public interface IActionCallExecutor {
    ResolvedAction execute(ActionCall call);
}
