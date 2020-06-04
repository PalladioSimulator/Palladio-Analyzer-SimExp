/**
 */
package de.fzi.srp.core.model.markovmodel.samplemodel;

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
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Trajectory#getSamplePath <em>Sample Path</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getTrajectory()
 * @model
 * @generated
 */
public interface Trajectory extends EObject {
	/**
	 * Returns the value of the '<em><b>Sample Path</b></em>' containment reference list.
	 * The list contents are of type {@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sample Path</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sample Path</em>' containment reference list.
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getTrajectory_SamplePath()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Sample> getSamplePath();

} // Trajectory
