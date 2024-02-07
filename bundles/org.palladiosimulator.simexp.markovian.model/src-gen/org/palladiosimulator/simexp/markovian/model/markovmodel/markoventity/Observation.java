/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

import org.eclipse.emf.common.util.EList;
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
public interface Observation<O> extends EObject {

    /**
     * Returns the value of the '<em><b>Observed</b></em>' reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State}<code>&lt;O&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Observed</em>' reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getObservation_Observed()
     * @model
     * @generated
     */
    EList<State<O>> getObserved();

} // Observation
