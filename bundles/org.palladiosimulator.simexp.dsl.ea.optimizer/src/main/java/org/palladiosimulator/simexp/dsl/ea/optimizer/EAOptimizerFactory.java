package org.palladiosimulator.simexp.dsl.ea.optimizer;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;

public class EAOptimizerFactory {
    public IEAOptimizer create(IEAConfig eaConfig) {
        return new EAOptimizer();
    }
}
