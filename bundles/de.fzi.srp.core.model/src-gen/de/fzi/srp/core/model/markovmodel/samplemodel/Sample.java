/**
 */
package de.fzi.srp.core.model.markovmodel.samplemodel;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.model.markovmodel.markoventity.Observation;
import de.fzi.srp.core.model.markovmodel.markoventity.Reward;
import de.fzi.srp.core.model.markovmodel.markoventity.State;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sample</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getReward <em>Reward</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getAction <em>Action</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getPointInTime <em>Point In Time</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getCurrent <em>Current</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getNext <em>Next</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getObservation <em>Observation</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample()
 * @model
 * @generated
 */
public interface Sample extends EObject {
	/**
	 * Returns the value of the '<em><b>Reward</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reward</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reward</em>' reference.
	 * @see #setReward(Reward)
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample_Reward()
	 * @model
	 * @generated
	 */
	Reward getReward();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getReward <em>Reward</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reward</em>' reference.
	 * @see #getReward()
	 * @generated
	 */
	void setReward(Reward value);

	/**
	 * Returns the value of the '<em><b>Action</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Action</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action</em>' reference.
	 * @see #setAction(Action)
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample_Action()
	 * @model
	 * @generated
	 */
	Action getAction();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getAction <em>Action</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' reference.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(Action value);

	/**
	 * Returns the value of the '<em><b>Point In Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Point In Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Point In Time</em>' attribute.
	 * @see #setPointInTime(int)
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample_PointInTime()
	 * @model required="true"
	 * @generated
	 */
	int getPointInTime();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getPointInTime <em>Point In Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Point In Time</em>' attribute.
	 * @see #getPointInTime()
	 * @generated
	 */
	void setPointInTime(int value);

	/**
	 * Returns the value of the '<em><b>Current</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Current</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Current</em>' reference.
	 * @see #setCurrent(State)
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample_Current()
	 * @model required="true"
	 * @generated
	 */
	State getCurrent();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getCurrent <em>Current</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Current</em>' reference.
	 * @see #getCurrent()
	 * @generated
	 */
	void setCurrent(State value);

	/**
	 * Returns the value of the '<em><b>Next</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Next</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Next</em>' reference.
	 * @see #setNext(State)
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample_Next()
	 * @model required="true"
	 * @generated
	 */
	State getNext();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getNext <em>Next</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Next</em>' reference.
	 * @see #getNext()
	 * @generated
	 */
	void setNext(State value);

	/**
	 * Returns the value of the '<em><b>Observation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Observation</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Observation</em>' reference.
	 * @see #setObservation(Observation)
	 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage#getSample_Observation()
	 * @model
	 * @generated
	 */
	Observation<?> getObservation();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.samplemodel.Sample#getObservation <em>Observation</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Observation</em>' reference.
	 * @see #getObservation()
	 * @generated
	 */
	void setObservation(Observation<?> value);

} // Sample
