/**
 */
package de.fzi.srp.core.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reward</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Reward#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getReward()
 * @model
 * @generated
 */
public interface Reward<U> extends EObject {
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
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getReward_Value()
	 * @model required="true"
	 * @generated
	 */
	U getValue();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Reward#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(U value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true"
	 * @generated
	 */
	Reward addWith(Reward other);

} // Reward
