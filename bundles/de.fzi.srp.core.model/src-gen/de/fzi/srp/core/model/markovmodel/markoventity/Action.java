/**
 */
package de.fzi.srp.core.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Action#getAction <em>Action</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getAction()
 * @model
 * @generated
 */
public interface Action<T> extends EObject {
	/**
	 * Returns the value of the '<em><b>Action</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Action</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action</em>' attribute.
	 * @see #setAction(Object)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getAction_Action()
	 * @model required="true"
	 * @generated
	 */
	T getAction();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Action#getAction <em>Action</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' attribute.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(T value);

} // Action
