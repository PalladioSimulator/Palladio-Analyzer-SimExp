package org.palladiosimulator.simexp.pcm.examples.udacitychallenge.reliability;

import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class ImageBlurMitigationStrategy extends ReconfigurationStrategy<QVToReconfiguration> implements Initializable {

	private final static String IMG_BLUR = "ImageBlurRandomVariable";
	
	protected boolean isFilteringActivated = false;
	
	@Override
	public String getId() {
		return ImageBlurMitigationStrategy.class.getCanonicalName();
	}

	@Override
	protected void monitor(State source, SharedKnowledge knowledge) {
		if ((source instanceof SelfAdaptiveSystemState<?>) == false) {
			throw new RuntimeException("");
		}

		var envState = ((SelfAdaptiveSystemState<?>) source).getPerceivedEnvironmentalState().getValue();
		for (InputValue each : UdacityEnvironmentalDynamics.toInputs(envState)) {
			if (each.variable.getEntityName().equals(IMG_BLUR)) {
				knowledge.store(IMG_BLUR, each.value);
			}
		}
	}

	@Override
	protected boolean analyse(State source, SharedKnowledge knowledge) {
		return true;
	}

	@Override
	protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
		var value = (CategoricalValue) knowledge.getValue(IMG_BLUR).orElseThrow();
		var isBlurryImage = value.get().equals("Blurry");
		
		if (isBlurryImage && isFilteringActivated) {
			return QVToReconfiguration.empty();
		} else if (isBlurryImage && !isFilteringActivated ) {
			return activateFilter(options);
		} else if (!isBlurryImage && isFilteringActivated) {
			return deactivateFilter(options);
		} else {
			return QVToReconfiguration.empty();
		}
	}

	@Override
	protected QVToReconfiguration emptyReconfiguration() {
		return QVToReconfiguration.empty();
	}

	@Override
	public void initialize() {
		isFilteringActivated = false;
	}

	protected QVToReconfiguration activateFilter(Set<QVToReconfiguration> options) {
		isFilteringActivated = true;

		return selectOptionWith("ActivateFilterComponent", options);
	}

	protected QVToReconfiguration deactivateFilter(Set<QVToReconfiguration> options) {
		isFilteringActivated = false;

		return selectOptionWith("DeactivateFilterComponent", options);
	}

	private QVToReconfiguration selectOptionWith(String queriedName, Set<QVToReconfiguration> options) {
		for (QVToReconfiguration each : options) {
			String reconfName = each.getStringRepresentation();
			if (reconfName.equals(queriedName)) {
				return each;
			}
		}

		throw new RuntimeException("There is no reconfiguration with name: " + queriedName);
	}

}
