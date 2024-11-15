package org.palladiosimulator.simexp.dsl.ea.optimizer;

import org.palladiosimulator.simexp.dsl.ea.api.IOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;

public class EAOptimizerFactory {
    public IOptimizer create() {
        return new EAOptimizer();
    }
}
