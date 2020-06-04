/**
 */
package de.fzi.srp.core.model.markovmodel.samplemodel;

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
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.SampleModel#getTrajectories <em>Trajectories</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSampleModel()
 * @model
 * @generated
 */
public interface SampleModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Trajectories</b></em>' containment reference list.
	 * The list contents are of type {@link de.fzi.srp.core.model.markovmodel.samplemodel.Trajectory}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Trajectories</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trajectories</em>' containment reference list.
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSampleModel_Trajectories()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Trajectory> getTrajectories();

} // SampleModel
