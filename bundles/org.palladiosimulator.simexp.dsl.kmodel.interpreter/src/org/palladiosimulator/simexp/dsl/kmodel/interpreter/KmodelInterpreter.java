package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simexp.core.strategy.mape.Analyzer;
import org.palladiosimulator.simexp.core.strategy.mape.Planner;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ActionCall;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IfStatement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.util.KmodelSwitch;

public class KmodelInterpreter extends KmodelSwitch<List<ResolvedAction>> implements Analyzer, Planner {
	
	private final Kmodel model;
	private final KmodelValueSwitch valueSwitch;

	public KmodelInterpreter(Kmodel model, VariableValueProvider vvp, ProbeValueProvider pvp, RuntimeValueProvider rvp) {
		this.model = model;
		this.valueSwitch = new KmodelValueSwitch(vvp, pvp, rvp);
	}
	
	@Override
	public boolean analyze() {
		return model.getStatements()
				.stream()
				.anyMatch(statement -> {
					IfStatement ifStatement = (IfStatement) statement;
					return ifStatement.isWithElse() || (boolean) getValue(ifStatement.getCondition());
				});
	}
	
	@Override
	public List<ResolvedAction> plan() {
		return doSwitch(model);
	}
	
	@Override
	public List<ResolvedAction> doSwitch(EObject object) {
		if (object == null) {
			throw new RuntimeException("Couldn't interpret an object that is null.");
		}
		
		List<ResolvedAction> result = super.doSwitch(object);
		
		if (result == null) {
			throw new RuntimeException("Couldn't interpret an object of class + '" + object.eClass().getName() + "'.");
		}
		
		return result;
	}
	
	@Override
	public List<ResolvedAction> caseKmodel(Kmodel model) {
		List<ResolvedAction> resolvedActions = new ArrayList<>();
		
		for (Statement statement : model.getStatements()) {
			resolvedActions.addAll(doSwitch(statement));
		}
		
		return resolvedActions;
	}
	
	@Override
	public List<ResolvedAction> caseIfStatement(IfStatement ifStatement) {
		List<ResolvedAction> resolvedActions = new ArrayList<>();
		boolean condition = (boolean) getValue(ifStatement.getCondition());
		
		if (condition) {
			for (Statement statement : ifStatement.getThenStatements()) {
				resolvedActions.addAll(doSwitch(statement));
			}
		} else if (ifStatement.isWithElse()) {
			for (Statement statement : ifStatement.getElseStatements()) {
				resolvedActions.addAll(doSwitch(statement));
			}
		}
		
		return resolvedActions;
 	}
	
	@Override
	public List<ResolvedAction> caseActionCall(ActionCall actionCall) {
		List<ResolvedAction> resolvedActions = new ArrayList<>();
		
		Action action = actionCall.getActionRef();
		Map<String, Object> arguments = resolveArguments(actionCall);
		ResolvedAction resolvedAction = new ResolvedAction(action, arguments);
		resolvedActions.add(resolvedAction);
		
		return resolvedActions;
	}
	
	public Object getValue(EObject object) {
		return valueSwitch.doSwitch(object);
	}
	
	private Map<String, Object> resolveArguments(ActionCall actionCall) {
		Map<String, Object> resolvedArguments = actionCall.getArguments()
				.stream()
				.collect(Collectors.toMap(arg -> arg.getParamRef().getName(), this::getValue));
		
		List<Field> variables = actionCall.getActionRef().getParameterList().getVariables();
		resolvedArguments.putAll(variables
				.stream()
				.collect(Collectors.toMap(Field::getName, this::getValue)));
		
		return resolvedArguments;
	}
}
