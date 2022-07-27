package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ActionCall;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IfStatement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Operation;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

public class KmodelInterpreter implements Analyzer, Planner {
	
	private final Kmodel model;
	private final ProbeValueProvider pvp;
	private final VariableValueProvider vvp;

	public KmodelInterpreter(Kmodel model, ProbeValueProvider pvp, VariableValueProvider vvp) {
		this.model = model;
		this.pvp = pvp;
		this.vvp = vvp;
	}
	
	@Override
	public boolean analyze() {
		List<Statement> statements = model.getStatements();
		
		if (statements.isEmpty()) {
			return false;
		}
		
		for (Statement statement : statements) {
			if (statement instanceof IfStatement) {
				IfStatement ifStatement = (IfStatement) statement;
				if ((boolean) getValue(ifStatement.getCondition())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public List<ResolvedAction> plan() {
		return getResolvedActions(model.getStatements());
	}
	
	private List<ResolvedAction> getResolvedActions(List<Statement> statements) {
		List<ResolvedAction> currentActions = new ArrayList<>();
		
		for (Statement statement : statements) {
			if (statement instanceof ActionCall) {
				ActionCall actionCall = (ActionCall) statement;
				Action action = actionCall.getActionRef();
				
				Map<String, Object> arguments = resolveArguments(actionCall);
				ResolvedAction resolvedAction = new ResolvedAction(action.getName(), arguments);
				
				currentActions.add(resolvedAction);
			}
			
			if (statement instanceof IfStatement) {
				IfStatement ifStatement = (IfStatement) statement;
				
				if ((boolean) getValue(ifStatement.getCondition())) {
					currentActions.addAll(getResolvedActions(ifStatement.getStatements()));
				}
			}
		}
		
		return currentActions;
	}
	
	public Map<String, Object> resolveArguments(ActionCall actionCall) {
		Map<String, Object> resolvedArguments = actionCall.getArguments().stream()
				.collect(Collectors.toMap(arg -> arg.getParamRef().getName(), arg -> getValue(arg.getArgument())));
		
		List<Field> variables = actionCall.getActionRef().getParameterList().getVariables();
		resolvedArguments.putAll(variables.stream()
				.collect(Collectors.toMap(Field::getName, var -> vvp.getValue((Variable) var))));
		
		return resolvedArguments;
	}
	
	public Object getValue(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		Object leftValue = getValue(expression.getLeft());
		Object rightValue = getValue(expression.getRight());
		
		Operation operation = expression.getOp();
		switch (operation) {
			case NULL:
				break;
				
			case OR:
				return (boolean) leftValue || (boolean) rightValue;
				
			case AND:
				return (boolean) leftValue && (boolean) rightValue;
				
			case EQUAL:
				return leftValue.equals(rightValue);
				
			case UNEQUAL:
				return !leftValue.equals(rightValue);
				
			case NOT:
				return !(boolean) leftValue;
				
			case SMALLER:
				return ((Number) leftValue).floatValue() < ((Number) rightValue).floatValue();
				
			case SMALLER_OR_EQUAL:
				return ((Number) leftValue).floatValue() <= ((Number) rightValue).floatValue();
				
			case GREATER_OR_EQUAL:	
				return ((Number) leftValue).floatValue() >= ((Number) rightValue).floatValue();
				
			case GREATER:
				return ((Number) leftValue).floatValue() > ((Number) rightValue).floatValue();
				
			case PLUS:
				return rightValue == null 
					? ((Number) leftValue).floatValue() 
					: ((Number) leftValue).floatValue() + ((Number) rightValue).floatValue();
				
			case MINUS:	
				return rightValue == null 
					? -((Number) leftValue).floatValue() 
					: ((Number) leftValue).floatValue() - ((Number) rightValue).floatValue();
				
			case MULTIPLY:
				return ((Number) leftValue).floatValue() * ((Number) rightValue).floatValue();
				
			case DIVIDE:
				return ((Number) leftValue).floatValue() / ((Number) rightValue).floatValue();
		}
		
		if (leftValue != null) {
			return leftValue;
		}
		
		Literal literal = expression.getLiteral();
		if (literal != null) {
			return getValue(literal);
		}
		
		Field field = expression.getFieldRef();
		if (field != null) {
			return getValue(field);
		}
		
		return null;
	}
	
	public Object getValue(Literal literal) {
		if (literal instanceof BoolLiteral) {
			return ((BoolLiteral) literal).isValue();
		}
		if (literal instanceof IntLiteral) {
			return ((IntLiteral) literal).getValue();
		}
		if (literal instanceof FloatLiteral) {
			return ((FloatLiteral) literal).getValue();
		}
		if (literal instanceof StringLiteral) {
			return ((StringLiteral) literal).getValue();
		}
		return null;
	}
	
	public Object getValue(Field field) {
		if (field instanceof Constant) {
			return getValue(((Constant) field).getValue());
		}
		if (field instanceof Variable) {
			return vvp.getValue((Variable) field);
		}
		if (field instanceof Probe) {
			return pvp.getValue((Probe) field);
		}
		return null;
	}
}
