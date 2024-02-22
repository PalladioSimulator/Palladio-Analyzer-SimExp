/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

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
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getReward()
 * @model
 * @generated
 */
public interface Reward<R> extends EObject {
    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(Object)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getReward_Value()
     * @model required="true"
     * @generated
     */
    R getValue();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
    void setValue(R value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model required="true"
     * @generated
     */
    Reward<R> addWith(Reward<R> other);

} // Reward
