package org.palladiosimulator.simexp.core.reward;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class SimulatedRewardReceiver implements RewardReceiver {
    
    private static final Logger LOGGER = Logger.getLogger(SimulatedRewardReceiver.class.getName());

	private final RewardEvaluator evaluator;
	
	private SimulatedRewardReceiver(RewardEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public static SimulatedRewardReceiver with(RewardEvaluator evaluator) {
		return new SimulatedRewardReceiver(evaluator);
	}

	@Override
	public Reward<?> obtain(Sample sample) {
		SelfAdaptiveSystemStateSampleValidator checkSample = this.new SelfAdaptiveSystemStateSampleValidator();
        try {
            checkSample.validate(sample);
        } catch (SimulatedRewardReceiver.SelfAdaptiveSystemStateSampleValidator.SelfAdaptiveSystemStateSampleValidationExcpetion e) {
            throw new RuntimeException(e);
        }
		
		return evaluate(sample);
	}

	private Reward<?> evaluate(Sample sample) {
		SelfAdaptiveSystemState<?> state = (SelfAdaptiveSystemState<?>) sample.getNext();
		Reward<?> evaluatedReward = evaluator.evaluate(state.getQuantifiedState());
		//LOGGER.debug(String.format(Locale.ENGLISH, "Evaluated reward: %.5f", evaluatedReward.getValue()));
        return evaluatedReward;
	}
	
	
	private class SelfAdaptiveSystemStateSampleValidator {
	    
        public void validate(Sample sample) throws SelfAdaptiveSystemStateSampleValidationExcpetion {
            boolean isValid = true;
            StringBuilder invalidSampleMsg = new StringBuilder("Self-adaptive system state sample is invalid. Reason: ");

            if(!(sample.getCurrent() instanceof SelfAdaptiveSystemState)){
                invalidSampleMsg.append("current state is of wrong type; expected to be of type SelfAdaptiveSystemState");
                isValid = false;
            } else if(!(sample.getNext() instanceof SelfAdaptiveSystemState)) {
                invalidSampleMsg.append("subsequent state is of wrong type; expected to be of type SelfAdaptiveSystemState");
                isValid = false;
            } if (!isValid) {
                throw new SelfAdaptiveSystemStateSampleValidationExcpetion(invalidSampleMsg.toString());
            }
        }

        public class SelfAdaptiveSystemStateSampleValidationExcpetion extends Exception {
            public SelfAdaptiveSystemStateSampleValidationExcpetion(String message) {
                super(message);
            }
        }
    }
	
}
