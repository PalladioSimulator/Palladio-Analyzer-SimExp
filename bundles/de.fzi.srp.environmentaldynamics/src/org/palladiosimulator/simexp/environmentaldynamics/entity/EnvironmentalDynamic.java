package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface EnvironmentalDynamic {

	public static class EnvironmentalDynamicBuilder {
		
		private MarkovModel model = null;
		private DerivableEnvironmentalDynamic dynamic = null;
		private boolean exploration = false;
		private boolean isHiddenProcess = false;

		private EnvironmentalDynamicBuilder(MarkovModel model) {
			this.model = model;
		}
		
		private EnvironmentalDynamicBuilder(DerivableEnvironmentalDynamic dynamic) {
			this.dynamic = dynamic;
		}

		public EnvironmentalDynamicBuilder asExplorationProcess() {
			this.exploration = true;
			return this;
		}

		public EnvironmentalDynamicBuilder asExploitationProcess() {
			this.exploration = false;
			return this;
		}

		public EnvironmentalDynamicBuilder isHiddenProcess() {
			this.isHiddenProcess = true;
			return this;
		}

		public EnvironmentalDynamic build() {
			StateSpaceNavigator navigator;
			if (model != null) {
				navigator = initDescribable();
			} else {
				navigator = initDerivable();
			}

			if (exploration) {
				EnvironmentalDynamic.class.cast(navigator).pursueExplorationStrategy();
			} else {
				EnvironmentalDynamic.class.cast(navigator).pursueExploitationStrategy();
			}

			return EnvironmentalDynamic.class.cast(navigator);

		}

		private StateSpaceNavigator initDerivable() {
			Objects.requireNonNull(dynamic, "The environemtal dynamic must not be null.");
			
			dynamic.isHiddenProcess = isHiddenProcess;
			return dynamic;
		}

		private StateSpaceNavigator initDescribable() {
			return new DescribableEnvironmentalDynamic(model, isHiddenProcess);
		}

	}

	public static EnvironmentalDynamicBuilder describedBy(MarkovModel model) {
		return new EnvironmentalDynamicBuilder(model);
	}
	
	public static EnvironmentalDynamicBuilder derivedBy(MarkovModel model) {
		return new EnvironmentalDynamicBuilder(model);
	}

	public void pursueExplorationStrategy();

	public void pursueExploitationStrategy();

	public boolean isHiddenProcess();

}
