/**
 */
package de.fzi.srp.core.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Observation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Observation#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getObservation()
 * @model
 * @generated
 */
public interface Observation<O> extends EObject {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(Object)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getObservation_Value()
	 * @model required="true"
	 * @generated
	 */
	O getValue();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Observation#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(O value);

} // Observation
