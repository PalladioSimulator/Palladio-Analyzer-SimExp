/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trajectory</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory#getSamplePath <em>Sample Path</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage#getTrajectory()
 * @model
 * @generated
 */
public interface Trajectory<A, R> extends EObject {
    /**
     * Returns the value of the '<em><b>Sample Path</b></em>' containment reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample}<code>&lt;A, R&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sample Path</em>' containment reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage#getTrajectory_SamplePath()
     * @model containment="true" required="true"
     * @generated
     */
    EList<Sample<A, R>> getSamplePath();

} // Trajectory
