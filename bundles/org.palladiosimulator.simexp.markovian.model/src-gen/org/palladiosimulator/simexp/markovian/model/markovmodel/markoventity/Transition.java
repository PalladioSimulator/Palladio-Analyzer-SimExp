/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

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
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getSource <em>Source</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getTarget <em>Target</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getLabel <em>Label</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getProbability <em>Probability</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getTransition()
 * @model
 * @generated
 */
public interface Transition<T, A> extends EObject {
    /**
     * Returns the value of the '<em><b>Source</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source</em>' reference.
     * @see #setSource(State)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Source()
     * @model required="true"
     * @generated
     */
    State<T> getSource();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getSource <em>Source</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' reference.
     * @see #getSource()
     * @generated
     */
    void setSource(State<T> value);

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target</em>' reference.
     * @see #setTarget(State)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Target()
     * @model required="true"
     * @generated
     */
    State<T> getTarget();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getTarget <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget(State<T> value);

    /**
     * Returns the value of the '<em><b>Label</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Label</em>' reference.
     * @see #setLabel(Action)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Label()
     * @model
     * @generated
     */
    Action<A> getLabel();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getLabel <em>Label</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Label</em>' reference.
     * @see #getLabel()
     * @generated
     */
    void setLabel(Action<A> value);

    /**
     * Returns the value of the '<em><b>Probability</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Probability</em>' attribute.
     * @see #setProbability(double)
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getTransition_Probability()
     * @model
     * @generated
     */
    double getProbability();

    /**
     * Sets the value of the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getProbability <em>Probability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Probability</em>' attribute.
     * @see #getProbability()
     * @generated
     */
    void setProbability(double value);

} // Transition
