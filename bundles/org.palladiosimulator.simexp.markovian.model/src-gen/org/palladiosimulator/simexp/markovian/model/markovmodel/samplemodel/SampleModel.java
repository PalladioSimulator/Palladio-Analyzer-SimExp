/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sample Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel#getTrajectories <em>Trajectories</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage#getSampleModel()
 * @model
 * @generated
 */
public interface SampleModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Trajectories</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trajectories</em>' containment reference list.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage#getSampleModel_Trajectories()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Trajectory> getTrajectories();

} // SampleModel
