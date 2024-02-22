/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Observation</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation#getObserved <em>Observed</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getObservation()
 * @model
 * @generated
 */
public interface Observation extends EObject {

    /**
     * Returns the value of the '<em><b>Observed</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Observed</em>' reference.
     * @see #setObserved(State)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getObservation_Observed()
     * @model
     * @generated
     */
    State getObserved();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation#getObserved <em>Observed</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Observed</em>' reference.
     * @see #getObserved()
     * @generated
     */
    void setObserved(State value);

} // Observation
