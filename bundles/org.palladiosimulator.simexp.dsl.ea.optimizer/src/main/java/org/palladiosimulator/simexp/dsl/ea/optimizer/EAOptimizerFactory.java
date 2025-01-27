package org.palladiosimulator.simexp.dsl.ea.optimizer;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;

public class EAOptimizerFactory {
    public IEAOptimizer create(IEAConfig config) {
        return new EAOptimizer();
    }

    public IEAOptimizer create() {
        return new EAOptimizer();
    }
}
