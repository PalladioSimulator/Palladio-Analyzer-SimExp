/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

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
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State#getProduces <em>Produces</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getState()
 * @model
 * @generated
 */
public interface State<T> extends EObject {
	/**
	 * Returns the value of the '<em><b>Produces</b></em>' reference list.
	 * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation}<code>&lt;T&gt;</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Produces</em>' reference list.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getState_Produces()
	 * @model
	 * @generated
	 */
	EList<Observation<T>> getProduces();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getState_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // State
