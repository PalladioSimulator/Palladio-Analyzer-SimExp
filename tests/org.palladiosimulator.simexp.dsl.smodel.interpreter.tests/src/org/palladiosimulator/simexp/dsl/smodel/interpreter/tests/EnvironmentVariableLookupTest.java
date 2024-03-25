package org.palladiosimulator.simexp.dsl.smodel.interpreter.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.lookup.EnvironmentVariableLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.util.EnvironmentalDynamicsCreatorHelper;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.util.EnvironmentalDynamicsTestModels;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.util.SModelCreatorHelper;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;

public class EnvironmentVariableLookupTest {

    private SModelCreatorHelper smodelCreator;
    private EnvironmentalDynamicsCreatorHelper envDynCreator;

    @Before
    public void setUp() throws Exception {
        smodelCreator = new SModelCreatorHelper();
        envDynCreator = new EnvironmentalDynamicsCreatorHelper();

    }

    @Test
    public void testFindStaticEnvVariable() {
        EnvironmentalDynamicsTestModels envDynModels = envDynCreator.createSimpleEnvDynamcisModels();
        ProbabilisticModelRepository staticEnvDynModel = envDynModels.getStaticEnvModel();
        GroundRandomVariable grv = staticEnvDynModel.getModels()
            .get(0)
            .getLocalProbabilisticModels()
            .get(0)
            .getGroundRandomVariables()
            .get(0);
        EnvironmentVariableLookup lookup = new EnvironmentVariableLookup(staticEnvDynModel);
        String expectedGRVId = grv.getId();
        EnvVariable envVar = smodelCreator.createEnvVariable(DataType.FLOAT, expectedGRVId, expectedGRVId);

        GroundRandomVariable actualGRV = lookup.findStaticEnvironmentVariable(envVar);

        assertEquals(expectedGRVId, actualGRV.getId());
    }

    // keine statische GRV in envdyn models
    // intertimelslice
    // intratimeslice
    // keine intertimelslice
    // keine intratimeslice

}
