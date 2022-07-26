package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.List;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;

public class KmodelInterpreter implements Analyzer, Planner {
	
	private Kmodel model;
	private ProbeValueProvider pvp;
	private VariableValueProvider vvp;

	public KmodelInterpreter(Kmodel model, ProbeValueProvider pvp, VariableValueProvider vvp) {
		this.model = model;
		this.pvp = pvp;
		this.vvp = vvp;
	}
	
	@Override
	public List<Action> plan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean analyze() {
		// TODO Auto-generated method stub
	    Object currentMeasurement = pvp.getValue(null);
	    
		return false;
	}
	
	
}
