/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage
 * @generated
 */
public interface MarkovEntityFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    MarkovEntityFactory eINSTANCE = org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityFactoryImpl
        .init();

    /**
     * Returns a new object of class '<em>Markov Model</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Markov Model</em>'.
     * @generated
     */
    <S, A, R> MarkovModel<S, A, R> createMarkovModel();

    /**
     * Returns a new object of class '<em>State</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>State</em>'.
     * @generated
     */
    <S> State<S> createState();

    /**
     * Returns a new object of class '<em>Observation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Observation</em>'.
     * @generated
     */
    <S> Observation<S> createObservation();

    /**
     * Returns a new object of class '<em>Transition</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Transition</em>'.
     * @generated
     */
    <T, A> Transition<T, A> createTransition();

    /**
     * Returns a new object of class '<em>Reward</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Reward</em>'.
     * @generated
     */
    <R> Reward<R> createReward();

    /**
     * Returns a new object of class '<em>Action</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Action</em>'.
     * @generated
     */
    <A> Action<A> createAction();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    MarkovEntityPackage getMarkovEntityPackage();

} //MarkovEntityFactory
