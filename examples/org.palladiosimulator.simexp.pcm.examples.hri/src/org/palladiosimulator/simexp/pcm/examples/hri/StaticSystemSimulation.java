package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.strategy.mape.Analyzer;
import org.palladiosimulator.simexp.core.strategy.mape.Executer;
import org.palladiosimulator.simexp.core.strategy.mape.Monitor;
import org.palladiosimulator.simexp.core.strategy.mape.Planner;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProviderMeasurementInjector;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public class StaticSystemSimulation extends ReconfigurationStrategy<QVToReconfiguration> {
	
	public StaticSystemSimulation(Monitor monitor, Analyzer analyzer, Planner planner, Executer executer, SimulatedMeasurementSpecification measurementSpec, ProbeValueProviderMeasurementInjector pvpInjector) {
		super(monitor, analyzer, planner, executer, measurementSpec, pvpInjector);
	}

	@Override
	public String getId() {
		return "StaticSystemSimulation";
	}

	@Override
	protected void monitor(State source, SharedKnowledge knowledge) {
		
	}

	@Override
	protected boolean analyse(State source, SharedKnowledge knowledge) {
		return false;
	}

	@Override
	protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
		return emptyReconfiguration();
	}

	@Override
	protected QVToReconfiguration emptyReconfiguration() {
		return QVToReconfiguration.empty();
	}

}
