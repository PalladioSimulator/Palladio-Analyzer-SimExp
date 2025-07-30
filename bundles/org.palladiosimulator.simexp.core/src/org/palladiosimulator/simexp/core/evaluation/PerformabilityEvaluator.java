package org.palladiosimulator.simexp.core.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;

public class PerformabilityEvaluator implements TotalRewardCalculation {

    private static final Logger LOGGER = Logger.getLogger(PerformabilityEvaluator.class.getName());

    private final ISimulatedExperienceAccessor accessor;

    private PerformabilityEvaluator(ISimulatedExperienceAccessor accessor) {
        this.accessor = accessor;
    }

    public static TotalRewardCalculation of(ISimulatedExperienceAccessor accessor) {
        return new PerformabilityEvaluator(accessor);
    }

    @Override
    public double computeTotalReward() {
        double totalReward = 0;

        List<Double> responseTimes = new ArrayList<>();
        SampleModelIterator iterator = SampleModelIterator.get(accessor);

        /**
         * total reward computation: aggregate data based on the performability metric, i.e.
         * calculate the expected value of all measured response times.
         * 
         */
        while (iterator.hasNext()) {
            for (SimulatedExperience exp : iterator.next()) {
                double responseTime = retrieveResponseTime(exp);
                responseTimes.add(responseTime);
            }
        }

        if (!responseTimes.isEmpty()) {
            double addedUpResponseTimes = responseTimes.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
            totalReward = addedUpResponseTimes / responseTimes.size();
        }

        LOGGER.debug(String.format(Locale.ENGLISH, "Computed performability reward: expectation(response time): %.5f",
                totalReward));
        return totalReward;
    }

    private double retrieveResponseTime(SimulatedExperience exp) {
        // response time reward format is of type double
        String reward = exp.getReward();
        return Double.parseDouble(reward);
    }

    @Override
    public String getName() {
        return "performability accumulated";
    }

}
