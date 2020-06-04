/**
 */
package de.fzi.srp.core.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getSource <em>Source</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getTarget <em>Target</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getLabel <em>Label</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getProbability <em>Probability</em>}</li>
 * </ul>
 *
 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getTransition()
 * @model
 * @generated
 */
public interface Transition extends EObject {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(State)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Source()
	 * @model required="true"
	 * @generated
	 */
	State getSource();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(State value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(State)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Target()
	 * @model required="true"
	 * @generated
	 */
	State getTarget();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(State value);

	/**
	 * Returns the value of the '<em><b>Label</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' reference.
	 * @see #setLabel(Action)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Label()
	 * @model
	 * @generated
	 */
	Action getLabel();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getLabel <em>Label</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' reference.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(Action value);

	/**
	 * Returns the value of the '<em><b>Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Probability</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Probability</em>' attribute.
	 * @see #setProbability(double)
	 * @see de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Probability()
	 * @model
	 * @generated
	 */
	double getProbability();

	/**
	 * Sets the value of the '{@link de.fzi.srp.core.model.markovmodel.markoventity.Transition#getProbability <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Probability</em>' attribute.
	 * @see #getProbability()
	 * @generated
	 */
	void setProbability(double value);

} // Transition
