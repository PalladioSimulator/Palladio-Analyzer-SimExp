package org.palladiosimulator.simexp.pcm.process;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;

class PcmExperienceSimulationRunnerTest {

    private PcmExperienceSimulationRunner simulationRunner;

    @BeforeEach
    void setUp() throws Exception {
        simulationRunner = new PcmExperienceSimulationRunner();
    }

    @Test
    void test() {
        SelfAdaptiveSystemState<?> sasState = null;
        simulationRunner.simulate(sasState);
        assertEquals(1, 2, "Failed to run test");
    }

}
