package org.palladiosimulator.simexp.dsl.kmodel.tests;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.junit.Assert;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KModel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Literal;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Operation;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.StringLiteral;

public class KmodelTestUtil {
	
	public static void assertModelWithoutErrors(KModel model) {
        Assert.assertNotNull(model);
        EList<Diagnostic> errors = model.eResource().getErrors();
        StringBuilder joinedErrors = new StringBuilder(); 
        for (Diagnostic diagnostic : errors) {
            joinedErrors.append(String.join(",", diagnostic.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected errors: %s", joinedErrors), errors.isEmpty());
    }
	
	public static void assertErrorMessages(KModel model, int numErrors, String... messages) {
    	Assert.assertNotNull(model);
    	EList<Diagnostic> errors = model.eResource().getErrors();
    	
    	Assert.assertEquals(numErrors, errors.size());
    	
    	for (int i = 0; i < numErrors; i++) {
    		String errorMessage = "Expected error: \"" + messages[i] + "\", got \"" + errors.get(i).getMessage() + "\" instead.";
    		Assert.assertEquals(errorMessage, messages[i], errors.get(i).getMessage());
    	}
    }
	
	public static void assertNoValidationIssues(ValidationTestHelper helper, KModel model) {
		Assert.assertNotNull(model);
		List<Issue> issues = helper.validate(model);
		StringBuilder joinedIssues = new StringBuilder(); 
        for (Issue issue : issues) {
            joinedIssues.append(String.join(",", issue.getMessage()));
        }
        Assert.assertTrue(String.format("Unexpected issues: %s", joinedIssues), issues.isEmpty());
	}
	
	public static void assertValidationIssues(ValidationTestHelper helper, KModel model, int numIssues, String... messages) {
    	Assert.assertNotNull(model);
    	List<Issue> issues = helper.validate(model);
    	
    	Assert.assertEquals(numIssues, issues.size());
    	
    	for (int i = 0; i < numIssues; i++) {
    		String errorMessage = "Expected issue: \"" + messages[i] + "\", got \"" + issues.get(i).getMessage() + "\" instead.";
    		Assert.assertEquals(errorMessage, messages[i], issues.get(i).getMessage());
    	}
    }
	
	public static Expression getNextExpressionWithContent(Expression expression) {
		Expression currentExpr = expression;
		
		while (currentExpr.getOp() == Operation.NULL && currentExpr.getExpr() == null
				&& currentExpr.getLiteral() == null && currentExpr.getFieldRef() == null) {
			
			currentExpr = currentExpr.getLeft();
		}
		
		return currentExpr;
	}
	
	public static DataType getDataType(Expression expression) {
		if (expression == null) {
			return null;
		}
		
		Operation operation = expression.getOp();
		
		if (operation != null) {
			switch (operation) {
				// No Operation.
				case NULL:
					break;
			
				// Fallthrough, all cases are boolean.
				case OR:
				case AND:
				case EQUAL:
				case UNEQUAL:
				case NOT:	
				case SMALLER:
				case SMALLER_OR_EQUAL:
				case GREATER_OR_EQUAL:
				case GREATER:	
					return DataType.BOOL;
			
				// Fallthrough, all cases are either int or float.
				case PLUS:
				case MINUS:
				case MULTIPLY:
					DataType leftDataType = getDataType(expression.getLeft());
					DataType rightDataType = getDataType(expression.getRight());
				
					if (leftDataType == DataType.FLOAT || rightDataType == DataType.FLOAT) {
						return DataType.FLOAT;
					} else {
					return DataType.INT;
					}
				
				// Division returns always a float value.	
				case DIVIDE:
					return DataType.FLOAT;
				
				default: 
					break;	
			}
		}
		
		Expression left = expression.getLeft();
		if (left != null) {
			return getDataType(left);
		}
		
		Expression inner = expression.getExpr();
		if (inner != null) {
			return getDataType(inner);
		}
		
		Literal literal = expression.getLiteral();
		if (literal != null) {
			if (literal instanceof BoolLiteral) {
				return DataType.BOOL;
				
			} else if (literal instanceof IntLiteral) {
				return DataType.INT;
				
			} else if (literal instanceof FloatLiteral) {
				return DataType.FLOAT;
				
			} else if (literal instanceof StringLiteral) {
				return DataType.STRING;
			}
		}
		
		Field fieldRef = expression.getFieldRef();
		if (fieldRef != null) {
			return fieldRef.getDataType();
		}
		
		return null;
	}
}
