package org.palladiosimulator.simexp.core.action;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public class ReconfigurationSelector implements Policy<Action<?>> {

	private final static String POLICY_NAME = "ReconfigurationDecoratorOfPolicy";
	
	private class ConversionHelper {
		
		private final State state;
		private final Set<Action<?>> actionSpace;
		
		public ConversionHelper(State state, Set<Action<?>> actionSpace) {
			this.state = state;
			this.actionSpace = actionSpace;
		}
		
		public Set<Reconfiguration<?>> asReconfigurations() {
			//TODO exception handling
			if (isNoReconfigurationSpace(actionSpace)) {
				throw new RuntimeException("");
			}
			
			return actionSpace.stream().map(each -> (Reconfiguration<?>) each)
									   .collect(Collectors.toSet());
		}
		
		public SelfAdaptiveSystemState<?> asSelfAdaptiveSystemState() {
			//TODO exception handling
			if (isNoSelfAdaptiveSystemState(state)) {
				throw new RuntimeException("");
			}
			
			return (SelfAdaptiveSystemState<?>) state;
		}

		private boolean isNoSelfAdaptiveSystemState(State state) {
			return (state instanceof SelfAdaptiveSystemState<?>) == false;
		}

		private boolean isNoReconfigurationSpace(Set<Action<?>> actionSpace) {
			return actionSpace.isEmpty() == false &&
				   (actionSpace.iterator().next() instanceof Reconfiguration<?>) == false;
		}
		
	}
	
	private final Optional<ReconfigurationFilter> filter;
	private final Policy<Action<?>> decoratedStrategy;
	
	public ReconfigurationSelector(Policy<Action<?>> decoratedStrategy) {
		this.filter = Optional.empty();
		this.decoratedStrategy = decoratedStrategy;
	}
	
	public ReconfigurationSelector(ReconfigurationFilter filter, Policy<Action<?>> decoratedStrategy) {
		this.filter = Optional.of(filter);
		this.decoratedStrategy = decoratedStrategy;
	}

	@Override
	public Action<?> select(State source, Set<Action<?>> options) {
		ConversionHelper helper = new ConversionHelper(source, options);
		Set<Action<?>> applicables = getFilter().filterApplicables(helper.asSelfAdaptiveSystemState(), helper.asReconfigurations());
		return decoratedStrategy.select(source, applicables);
	}

	private ReconfigurationFilter getFilter() {
		return filter.orElse(new DefaultReconfigurationFilter());
	}

	@Override
	public String getId() {
		return POLICY_NAME + decoratedStrategy.getId();
	}
	
}
