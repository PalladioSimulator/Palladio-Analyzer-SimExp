package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;

public interface IActionCallExecutor {
    ResolvedAction execute(ActionCall call);
}
