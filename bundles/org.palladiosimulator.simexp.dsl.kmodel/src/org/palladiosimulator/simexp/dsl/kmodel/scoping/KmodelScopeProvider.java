/*
 * generated by Xtext 2.26.0
 */
package org.palladiosimulator.simexp.dsl.kmodel.scoping;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.FilteringScope;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ActionCall;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ArgumentKeyValue;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Field;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Parameter;

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
public class KmodelScopeProvider extends AbstractKmodelScopeProvider {
	
	@Override
	public IScope getScope(EObject context, EReference reference) {
		if (context instanceof Expression) {
			EObject rootElement = EcoreUtil2.getRootContainer(context);
			List<Field> fields = EcoreUtil2.getAllContentsOfType(rootElement, Field.class);
			
			IScope scope = Scopes.scopeFor(fields);
			
			return new FilteringScope(scope, field -> fieldDeclaredBefore(field.getEObjectOrProxy(), context));
		}
		
		if (context instanceof ArgumentKeyValue) {
			EObject rootElement = EcoreUtil2.getRootContainer(context);
			List<Parameter> parameters = EcoreUtil2.getAllContentsOfType(rootElement, Parameter.class);
			
			IScope scope = Scopes.scopeFor(parameters);
			
			ActionCall parentStatement = EcoreUtil2.getContainerOfType(context, ActionCall.class);
			Action actionRef = parentStatement.getActionRef();
			List<Parameter> scopeParameters = EcoreUtil2.getAllContentsOfType(actionRef, Parameter.class);
			
			return new FilteringScope(scope, param -> scopeParameters.contains(param.getEObjectOrProxy()));
		}
		
		return super.getScope(context, reference);
	}
	
	/*
	 * Returns true iff the field was declared before beeing referenced in the expression.
	 */
	private boolean fieldDeclaredBefore(EObject field, EObject expression) {
		Kmodel kmodel = EcoreUtil2.getContainerOfType(expression, Kmodel.class);
		List<EObject> contents = EcoreUtil2.eAllContentsAsList(kmodel);
		List<EObject> fieldsDefinedBefore;
		
		try {
			EObject first = contents
					.stream()
					.filter(object -> EcoreUtil.isAncestor(object, expression))
					.findFirst()
					.get();
			fieldsDefinedBefore = contents.subList(0 , contents.indexOf(first));
		
		} catch (NoSuchElementException e) {
			fieldsDefinedBefore = Collections.emptyList();
		}
		
		return fieldsDefinedBefore.contains(field);
	}
}