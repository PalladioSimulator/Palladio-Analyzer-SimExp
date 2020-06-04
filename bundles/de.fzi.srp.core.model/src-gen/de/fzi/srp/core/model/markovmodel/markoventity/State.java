/**
 */
package de.fzi.srp.core.model.markovmodel.markoventity;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.State#getProduces <em>Produces</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.State#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getState()
 * @model
 * @generated
 */
public interface State extends EObject {
	/**
	 * Returns the value of the '<em><b>Produces</b></em>' reference list.
	 * The list contents are of type {@link de.fzi.srp.core.model.markovmodel.markoventity.Observation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Produces</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Produces</em>' reference list.
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getState_Produces()
	 * @model
	 * @generated
	 */
	EList<Observation> getProduces();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getState_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.State#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // State
