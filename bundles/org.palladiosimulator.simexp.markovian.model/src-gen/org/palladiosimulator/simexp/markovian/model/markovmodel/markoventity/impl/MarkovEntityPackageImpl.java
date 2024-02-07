/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MarkovEntityPackageImpl extends EPackageImpl implements MarkovEntityPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass markovModelEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass stateEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass observationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass transitionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass rewardEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass actionEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private MarkovEntityPackageImpl() {
        super(eNS_URI, MarkovEntityFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     *
     * <p>This method is used to initialize {@link MarkovEntityPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static MarkovEntityPackage init() {
        if (isInited)
            return (MarkovEntityPackage) EPackage.Registry.INSTANCE.getEPackage(MarkovEntityPackage.eNS_URI);

        // Obtain or create and register package
        Object registeredMarkovEntityPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
        MarkovEntityPackageImpl theMarkovEntityPackage = registeredMarkovEntityPackage instanceof MarkovEntityPackageImpl
                ? (MarkovEntityPackageImpl) registeredMarkovEntityPackage
                : new MarkovEntityPackageImpl();

        isInited = true;

        // Obtain or create and register interdependencies
        Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(SampleModelPackage.eNS_URI);
        SampleModelPackageImpl theSampleModelPackage = (SampleModelPackageImpl) (registeredPackage instanceof SampleModelPackageImpl
                ? registeredPackage
                : SampleModelPackage.eINSTANCE);

        // Create package meta-data objects
        theMarkovEntityPackage.createPackageContents();
        theSampleModelPackage.createPackageContents();

        // Initialize created meta-data
        theMarkovEntityPackage.initializePackageContents();
        theSampleModelPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theMarkovEntityPackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(MarkovEntityPackage.eNS_URI, theMarkovEntityPackage);
        return theMarkovEntityPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getMarkovModel() {
        return markovModelEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getMarkovModel_Transitions() {
        return (EReference) markovModelEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getMarkovModel_StateSpace() {
        return (EReference) markovModelEClass.getEStructuralFeatures()
            .get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getMarkovModel_Reward() {
        return (EReference) markovModelEClass.getEStructuralFeatures()
            .get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getMarkovModel_Actions() {
        return (EReference) markovModelEClass.getEStructuralFeatures()
            .get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getMarkovModel_Observations() {
        return (EReference) markovModelEClass.getEStructuralFeatures()
            .get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getState() {
        return stateEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getState_Name() {
        return (EAttribute) stateEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getObservation() {
        return observationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getObservation_Observed() {
        return (EReference) observationEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getTransition() {
        return transitionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getTransition_Source() {
        return (EReference) transitionEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getTransition_Target() {
        return (EReference) transitionEClass.getEStructuralFeatures()
            .get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getTransition_Label() {
        return (EReference) transitionEClass.getEStructuralFeatures()
            .get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTransition_Probability() {
        return (EAttribute) transitionEClass.getEStructuralFeatures()
            .get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getReward() {
        return rewardEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getReward_Value() {
        return (EAttribute) rewardEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EOperation getReward__AddWith__Reward() {
        return rewardEClass.getEOperations()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getAction() {
        return actionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getAction_Action() {
        return (EAttribute) actionEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public MarkovEntityFactory getMarkovEntityFactory() {
        return (MarkovEntityFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated)
            return;
        isCreated = true;

        // Create classes and their features
        markovModelEClass = createEClass(MARKOV_MODEL);
        createEReference(markovModelEClass, MARKOV_MODEL__TRANSITIONS);
        createEReference(markovModelEClass, MARKOV_MODEL__STATE_SPACE);
        createEReference(markovModelEClass, MARKOV_MODEL__REWARD);
        createEReference(markovModelEClass, MARKOV_MODEL__ACTIONS);
        createEReference(markovModelEClass, MARKOV_MODEL__OBSERVATIONS);

        stateEClass = createEClass(STATE);
        createEAttribute(stateEClass, STATE__NAME);

        observationEClass = createEClass(OBSERVATION);
        createEReference(observationEClass, OBSERVATION__OBSERVED);

        transitionEClass = createEClass(TRANSITION);
        createEReference(transitionEClass, TRANSITION__SOURCE);
        createEReference(transitionEClass, TRANSITION__TARGET);
        createEReference(transitionEClass, TRANSITION__LABEL);
        createEAttribute(transitionEClass, TRANSITION__PROBABILITY);

        rewardEClass = createEClass(REWARD);
        createEAttribute(rewardEClass, REWARD__VALUE);
        createEOperation(rewardEClass, REWARD___ADD_WITH__REWARD);

        actionEClass = createEClass(ACTION);
        createEAttribute(actionEClass, ACTION__ACTION);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized)
            return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Create type parameters
        ETypeParameter markovModelEClass_S = addETypeParameter(markovModelEClass, "S");
        ETypeParameter markovModelEClass_A = addETypeParameter(markovModelEClass, "A");
        ETypeParameter markovModelEClass_R = addETypeParameter(markovModelEClass, "R");
        addETypeParameter(stateEClass, "S");
        ETypeParameter observationEClass_O = addETypeParameter(observationEClass, "O");
        ETypeParameter transitionEClass_S = addETypeParameter(transitionEClass, "S");
        ETypeParameter transitionEClass_A = addETypeParameter(transitionEClass, "A");
        ETypeParameter rewardEClass_R = addETypeParameter(rewardEClass, "R");
        ETypeParameter actionEClass_A = addETypeParameter(actionEClass, "A");

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass(markovModelEClass, MarkovModel.class, "MarkovModel", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        EGenericType g1 = createEGenericType(this.getTransition());
        EGenericType g2 = createEGenericType(markovModelEClass_S);
        g1.getETypeArguments()
            .add(g2);
        g2 = createEGenericType(markovModelEClass_A);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getMarkovModel_Transitions(), g1, null, "transitions", null, 1, -1, MarkovModel.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        g1 = createEGenericType(this.getState());
        g2 = createEGenericType(markovModelEClass_S);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getMarkovModel_StateSpace(), g1, null, "stateSpace", null, 1, -1, MarkovModel.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        g1 = createEGenericType(this.getReward());
        g2 = createEGenericType(markovModelEClass_R);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getMarkovModel_Reward(), g1, null, "reward", null, 0, -1, MarkovModel.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        g1 = createEGenericType(this.getAction());
        g2 = createEGenericType(markovModelEClass_A);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getMarkovModel_Actions(), g1, null, "actions", null, 0, -1, MarkovModel.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        g1 = createEGenericType(this.getObservation());
        g2 = createEGenericType(markovModelEClass_S);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getMarkovModel_Observations(), g1, null, "observations", null, 0, -1, MarkovModel.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(stateEClass, State.class, "State", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getState_Name(), ecorePackage.getEString(), "name", null, 0, 1, State.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(observationEClass, Observation.class, "Observation", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(this.getState());
        g2 = createEGenericType(observationEClass_O);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getObservation_Observed(), g1, null, "observed", null, 0, 1, Observation.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        initEClass(transitionEClass, Transition.class, "Transition", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(this.getState());
        g2 = createEGenericType(transitionEClass_S);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getTransition_Source(), g1, null, "source", null, 1, 1, Transition.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        g1 = createEGenericType(this.getState());
        g2 = createEGenericType(transitionEClass_S);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getTransition_Target(), g1, null, "target", null, 1, 1, Transition.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        g1 = createEGenericType(this.getAction());
        g2 = createEGenericType(transitionEClass_A);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getTransition_Label(), g1, null, "label", null, 0, 1, Transition.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(getTransition_Probability(), ecorePackage.getEDouble(), "probability", null, 0, 1,
                Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);

        initEClass(rewardEClass, Reward.class, "Reward", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(rewardEClass_R);
        initEAttribute(getReward_Value(), g1, "value", null, 1, 1, Reward.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = initEOperation(getReward__AddWith__Reward(), null, "addWith", 1, 1, IS_UNIQUE, IS_ORDERED);
        g1 = createEGenericType(this.getReward());
        g2 = createEGenericType(rewardEClass_R);
        g1.getETypeArguments()
            .add(g2);
        addEParameter(op, g1, "other", 0, 1, IS_UNIQUE, IS_ORDERED);
        g1 = createEGenericType(this.getReward());
        g2 = createEGenericType(rewardEClass_R);
        g1.getETypeArguments()
            .add(g2);
        initEOperation(op, g1);

        initEClass(actionEClass, Action.class, "Action", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(actionEClass_A);
        initEAttribute(getAction_Action(), g1, "action", null, 1, 1, Action.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //MarkovEntityPackageImpl
