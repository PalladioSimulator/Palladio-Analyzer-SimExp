package org.palladiosimulator.simexp.dsl.ea.optimizer;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.EAOptimizerOneHotEncoding;

public class EAOptimizerFactory {
    public IEAOptimizer create(IEAConfig eaConfig) {
        switch (eaConfig.getEncoding()) {
        case GrayEncoding:
            return new EAOptimizer();
        case OneHotEncoding:
            return new EAOptimizerOneHotEncoding();
        default:
            return new EAOptimizer();
        }

    }
}
