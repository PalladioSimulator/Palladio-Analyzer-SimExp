package org.palladiosimulator.simexp.core.reward;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class SimulatedRewardReceiver<C, A, R, V> implements RewardReceiver<A, R> {

    private static final Logger LOGGER = Logger.getLogger(SimulatedRewardReceiver.class.getName());

    private final RewardEvaluator<R> evaluator;

    private SimulatedRewardReceiver(RewardEvaluator<R> evaluator) {
        this.evaluator = evaluator;
    }

    public static <C, A, R, V> SimulatedRewardReceiver<C, A, R, V> with(RewardEvaluator<R> evaluator) {
        return new SimulatedRewardReceiver<>(evaluator);
    }

    @Override
    public Reward<R> obtain(Sample<A, R> sample) {
        SelfAdaptiveSystemStateSampleValidator checkSample = this.new SelfAdaptiveSystemStateSampleValidator();
        try {
            checkSample.validate(sample);
        } catch (SimulatedRewardReceiver<C, A, R, V>.SelfAdaptiveSystemStateSampleValidator.SelfAdaptiveSystemStateSampleValidationExcpetion e) {
            throw new RuntimeException(e);
        }

        return evaluate(sample);
    }

    private Reward<R> evaluate(Sample<A, R> sample) {
        SelfAdaptiveSystemState<C, A, V> state = (SelfAdaptiveSystemState<C, A, V>) sample.getNext();
        Reward<R> evaluatedReward = evaluator.evaluate(state.getQuantifiedState());

        LOGGER.debug(String.format(Locale.ENGLISH, "Evaluated reward: %s", evaluatedReward.getValue()
            .toString()));

        return evaluatedReward;
    }

    private class SelfAdaptiveSystemStateSampleValidator {

        public void validate(Sample<A, R> sample) throws SelfAdaptiveSystemStateSampleValidationExcpetion {
            boolean isValid = true;
            StringBuilder invalidSampleMsg = new StringBuilder(
                    "Self-adaptive system state sample is invalid. Reason: ");

            if (!(sample.getCurrent() instanceof SelfAdaptiveSystemState)) {
                invalidSampleMsg
                    .append("current state is of wrong type; expected to be of type SelfAdaptiveSystemState");
                isValid = false;
            } else if (!(sample.getNext() instanceof SelfAdaptiveSystemState)) {
                invalidSampleMsg
                    .append("subsequent state is of wrong type; expected to be of type SelfAdaptiveSystemState");
                isValid = false;
            }
            if (!isValid) {
                throw new SelfAdaptiveSystemStateSampleValidationExcpetion(invalidSampleMsg.toString());
            }
        }

        public static class SelfAdaptiveSystemStateSampleValidationExcpetion extends Exception {
            public SelfAdaptiveSystemStateSampleValidationExcpetion(String message) {
                super(message);
            }
        }
    }

}
