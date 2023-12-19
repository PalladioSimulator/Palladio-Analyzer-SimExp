/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

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
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action#getAction <em>Action</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getAction()
 * @model
 * @generated
 */
public interface Action<A> extends EObject {
    /**
     * Returns the value of the '<em><b>Action</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Action</em>' attribute.
     * @see #setAction(Object)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getAction_Action()
     * @model required="true"
     * @generated
     */
    A getAction();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action#getAction <em>Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Action</em>' attribute.
     * @see #getAction()
     * @generated
     */
    void setAction(A value);

} // Action
