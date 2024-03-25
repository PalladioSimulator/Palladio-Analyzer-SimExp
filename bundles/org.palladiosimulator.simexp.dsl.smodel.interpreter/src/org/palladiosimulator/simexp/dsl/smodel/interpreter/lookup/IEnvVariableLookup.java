package org.palladiosimulator.simexp.dsl.smodel.interpreter.lookup;

import org.palladiosimulator.envdyn.environment.dynamicmodel.TimeSliceInduction;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;

public interface IEnvVariableLookup {

    GroundRandomVariable findStaticEnvironmentVariable(EnvVariable envVar);

    TimeSliceInduction findDynamicEnvironmentVariable(EnvVariable envVar);

}
