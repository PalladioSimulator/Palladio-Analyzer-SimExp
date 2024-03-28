package org.palladiosimulator.simexp.dsl.smodel.interpreter.util;

import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

public class PcmModelsCreatorHelper {

    private final UsagemodelFactory usageModelFactory = UsagemodelFactory.eINSTANCE;

    public PcmModelsCreatorHelper() {
        // TODO Auto-generated constructor stub
    }

    public UsageModel createBasicUsageScenario() {
        UsageModel usageModel = usageModelFactory.createUsageModel();
        return usageModel;

    }

}
