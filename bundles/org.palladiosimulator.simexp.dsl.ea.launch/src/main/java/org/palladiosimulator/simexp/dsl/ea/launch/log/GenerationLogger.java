package org.palladiosimulator.simexp.dsl.ea.launch.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.util.IRewardFormater;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class GenerationLogger implements IEAEvolutionStatusReceiver {
    private static final Logger LOGGER = Logger.getLogger(GenerationLogger.class);

    private final IRewardFormater rewardFormater;

    public GenerationLogger(IRewardFormater rewardFormater) {
        this.rewardFormater = rewardFormater;
    }

    @Override
    public void reportStatus(long generation, List<OptimizableValue<?>> optimizableValues, double fitness) {
        LOGGER.info(String.format("fitness status in generation %d for: %s = %s", generation,
                asString(optimizableValues), rewardFormater.asString(fitness)));
    }

    private String asString(List<OptimizableValue<?>> optimizableValues) {
        List<String> entries = new ArrayList<>();
        for (OptimizableValue<?> ov : optimizableValues) {
            entries.add(String.format("%s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        return StringUtils.join(entries, ",");
    }

    @Override
    public void close() {
    }
}
