/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory
 * @model kind="package"
 * @generated
 */
public interface MarkovEntityPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "markoventity";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://palladiosimulator.org/markovmodel/markoventity/1.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "markoventity";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    MarkovEntityPackage eINSTANCE = org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl
        .init();

    /**
     * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl <em>Markov Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getMarkovModel()
     * @generated
     */
    int MARKOV_MODEL = 0;

    /**
     * The feature id for the '<em><b>Transitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL__TRANSITIONS = 0;

    /**
     * The feature id for the '<em><b>State Space</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL__STATE_SPACE = 1;

    /**
     * The feature id for the '<em><b>Reward</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL__REWARD = 2;

    /**
     * The feature id for the '<em><b>Actions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL__ACTIONS = 3;

    /**
     * The feature id for the '<em><b>Observations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL__OBSERVATIONS = 4;

    /**
     * The number of structural features of the '<em>Markov Model</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Markov Model</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MARKOV_MODEL_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl <em>State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getState()
     * @generated
     */
    int STATE = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__NAME = 0;

    /**
     * The number of structural features of the '<em>State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl <em>Observation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getObservation()
     * @generated
     */
    int OBSERVATION = 2;

    /**
     * The feature id for the '<em><b>Observed</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OBSERVATION__OBSERVED = 0;

    /**
     * The number of structural features of the '<em>Observation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OBSERVATION_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Observation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OBSERVATION_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl <em>Transition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getTransition()
     * @generated
     */
    int TRANSITION = 3;

    /**
     * The feature id for the '<em><b>Source</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__SOURCE = 0;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__TARGET = 1;

    /**
     * The feature id for the '<em><b>Label</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__LABEL = 2;

    /**
     * The feature id for the '<em><b>Probability</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__PROBABILITY = 3;

    /**
     * The number of structural features of the '<em>Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl <em>Reward</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getReward()
     * @generated
     */
    int REWARD = 4;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REWARD__VALUE = 0;

    /**
     * The number of structural features of the '<em>Reward</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REWARD_FEATURE_COUNT = 1;

    /**
     * The operation id for the '<em>Add With</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REWARD___ADD_WITH__REWARD = 0;

    /**
     * The number of operations of the '<em>Reward</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REWARD_OPERATION_COUNT = 1;

    /**
     * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl <em>Action</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getAction()
     * @generated
     */
    int ACTION = 5;

    /**
     * The feature id for the '<em><b>Action</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTION__ACTION = 0;

    /**
     * The number of structural features of the '<em>Action</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTION_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Action</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTION_OPERATION_COUNT = 0;

    /**
     * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel <em>Markov Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Markov Model</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel
     * @generated
     */
    EClass getMarkovModel();

    /**
     * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getTransitions <em>Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Transitions</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getTransitions()
     * @see #getMarkovModel()
     * @generated
     */
    EReference getMarkovModel_Transitions();

    /**
     * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getStateSpace <em>State Space</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>State Space</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getStateSpace()
     * @see #getMarkovModel()
     * @generated
     */
    EReference getMarkovModel_StateSpace();

    /**
     * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getReward <em>Reward</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Reward</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getReward()
     * @see #getMarkovModel()
     * @generated
     */
    EReference getMarkovModel_Reward();

    /**
     * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getActions <em>Actions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Actions</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getActions()
     * @see #getMarkovModel()
     * @generated
     */
    EReference getMarkovModel_Actions();

    /**
     * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getObservations <em>Observations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Observations</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel#getObservations()
     * @see #getMarkovModel()
     * @generated
     */
    EReference getMarkovModel_Observations();

    /**
     * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>State</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State
     * @generated
     */
    EClass getState();

    /**
     * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State#getName()
     * @see #getState()
     * @generated
     */
    EAttribute getState_Name();

    /**
     * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation <em>Observation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Observation</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation
     * @generated
     */
    EClass getObservation();

    /**
     * Returns the meta object for the reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation#getObserved <em>Observed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Observed</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation#getObserved()
     * @see #getObservation()
     * @generated
     */
    EReference getObservation_Observed();

    /**
     * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition <em>Transition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Transition</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition
     * @generated
     */
    EClass getTransition();

    /**
     * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Source</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getSource()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_Source();

    /**
     * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getTarget()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_Target();

    /**
     * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getLabel <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Label</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getLabel()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_Label();

    /**
     * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getProbability <em>Probability</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Probability</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition#getProbability()
     * @see #getTransition()
     * @generated
     */
    EAttribute getTransition_Probability();

    /**
     * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward <em>Reward</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Reward</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward
     * @generated
     */
    EClass getReward();

    /**
     * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward#getValue()
     * @see #getReward()
     * @generated
     */
    EAttribute getReward_Value();

    /**
     * Returns the meta object for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward#addWith(org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward) <em>Add With</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Add With</em>' operation.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward#addWith(org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward)
     * @generated
     */
    EOperation getReward__AddWith__Reward();

    /**
     * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action <em>Action</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Action</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action
     * @generated
     */
    EClass getAction();

    /**
     * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action#getAction <em>Action</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Action</em>'.
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action#getAction()
     * @see #getAction()
     * @generated
     */
    EAttribute getAction_Action();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    MarkovEntityFactory getMarkovEntityFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl <em>Markov Model</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getMarkovModel()
         * @generated
         */
        EClass MARKOV_MODEL = eINSTANCE.getMarkovModel();

        /**
         * The meta object literal for the '<em><b>Transitions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MARKOV_MODEL__TRANSITIONS = eINSTANCE.getMarkovModel_Transitions();

        /**
         * The meta object literal for the '<em><b>State Space</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MARKOV_MODEL__STATE_SPACE = eINSTANCE.getMarkovModel_StateSpace();

        /**
         * The meta object literal for the '<em><b>Reward</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MARKOV_MODEL__REWARD = eINSTANCE.getMarkovModel_Reward();

        /**
         * The meta object literal for the '<em><b>Actions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MARKOV_MODEL__ACTIONS = eINSTANCE.getMarkovModel_Actions();

        /**
         * The meta object literal for the '<em><b>Observations</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MARKOV_MODEL__OBSERVATIONS = eINSTANCE.getMarkovModel_Observations();

        /**
         * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl <em>State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getState()
         * @generated
         */
        EClass STATE = eINSTANCE.getState();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATE__NAME = eINSTANCE.getState_Name();

        /**
         * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl <em>Observation</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getObservation()
         * @generated
         */
        EClass OBSERVATION = eINSTANCE.getObservation();

        /**
         * The meta object literal for the '<em><b>Observed</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference OBSERVATION__OBSERVED = eINSTANCE.getObservation_Observed();

        /**
         * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl <em>Transition</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getTransition()
         * @generated
         */
        EClass TRANSITION = eINSTANCE.getTransition();

        /**
         * The meta object literal for the '<em><b>Source</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__SOURCE = eINSTANCE.getTransition_Source();

        /**
         * The meta object literal for the '<em><b>Target</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__TARGET = eINSTANCE.getTransition_Target();

        /**
         * The meta object literal for the '<em><b>Label</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__LABEL = eINSTANCE.getTransition_Label();

        /**
         * The meta object literal for the '<em><b>Probability</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TRANSITION__PROBABILITY = eINSTANCE.getTransition_Probability();

        /**
         * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl <em>Reward</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getReward()
         * @generated
         */
        EClass REWARD = eINSTANCE.getReward();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute REWARD__VALUE = eINSTANCE.getReward_Value();

        /**
         * The meta object literal for the '<em><b>Add With</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation REWARD___ADD_WITH__REWARD = eINSTANCE.getReward__AddWith__Reward();

        /**
         * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl <em>Action</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ActionImpl
         * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl#getAction()
         * @generated
         */
        EClass ACTION = eINSTANCE.getAction();

        /**
         * The meta object literal for the '<em><b>Action</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ACTION__ACTION = eINSTANCE.getAction_Action();

    }

} //MarkovEntityPackage
