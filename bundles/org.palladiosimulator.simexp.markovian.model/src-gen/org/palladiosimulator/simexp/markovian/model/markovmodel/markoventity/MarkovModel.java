/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Markov Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getTransitions <em>Transitions</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getStateSpace <em>State Space</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getReward <em>Reward</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getActions <em>Actions</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getObservations <em>Observations</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getMarkovModel()
 * @model
 * @generated
 */
public interface MarkovModel<S, A, R> extends EObject {
    /**
     * Returns the value of the '<em><b>Transitions</b></em>' containment reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition}<code>&lt;S, A&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Transitions</em>' containment reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getMarkovModel_Transitions()
     * @model containment="true" required="true"
     * @generated
     */
    EList<Transition<S, A>> getTransitions();

    /**
     * Returns the value of the '<em><b>State Space</b></em>' containment reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State}<code>&lt;S&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>State Space</em>' containment reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getMarkovModel_StateSpace()
     * @model containment="true" required="true"
     * @generated
     */
    EList<State<S>> getStateSpace();

    /**
     * Returns the value of the '<em><b>Reward</b></em>' containment reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward}<code>&lt;R&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Reward</em>' containment reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getMarkovModel_Reward()
     * @model containment="true"
     * @generated
     */
    EList<Reward<R>> getReward();

    /**
     * Returns the value of the '<em><b>Actions</b></em>' containment reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action}<code>&lt;A&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Actions</em>' containment reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getMarkovModel_Actions()
     * @model containment="true"
     * @generated
     */
    EList<Action<A>> getActions();

    /**
     * Returns the value of the '<em><b>Observations</b></em>' containment reference list.
     * The list contents are of type {@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation}<code>&lt;S&gt;</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Observations</em>' containment reference list.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#getMarkovModel_Observations()
     * @model containment="true"
     * @generated
     */
    EList<Observation<S>> getObservations();

} // MarkovModel
