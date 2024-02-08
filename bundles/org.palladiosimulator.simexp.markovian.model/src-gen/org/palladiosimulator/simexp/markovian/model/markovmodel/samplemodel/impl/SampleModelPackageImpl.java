/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovEntityPackageImpl;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SampleModelPackageImpl extends EPackageImpl implements SampleModelPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass trajectoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass sampleEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass sampleModelEClass = null;

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
     * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private SampleModelPackageImpl() {
        super(eNS_URI, SampleModelFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link SampleModelPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static SampleModelPackage init() {
        if (isInited)
            return (SampleModelPackage) EPackage.Registry.INSTANCE.getEPackage(SampleModelPackage.eNS_URI);

        // Obtain or create and register package
        Object registeredSampleModelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
        SampleModelPackageImpl theSampleModelPackage = registeredSampleModelPackage instanceof SampleModelPackageImpl
                ? (SampleModelPackageImpl) registeredSampleModelPackage
                : new SampleModelPackageImpl();

        isInited = true;

        // Obtain or create and register interdependencies
        Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(MarkovEntityPackage.eNS_URI);
        MarkovEntityPackageImpl theMarkovEntityPackage = (MarkovEntityPackageImpl) (registeredPackage instanceof MarkovEntityPackageImpl
                ? registeredPackage
                : MarkovEntityPackage.eINSTANCE);

        // Create package meta-data objects
        theSampleModelPackage.createPackageContents();
        theMarkovEntityPackage.createPackageContents();

        // Initialize created meta-data
        theSampleModelPackage.initializePackageContents();
        theMarkovEntityPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theSampleModelPackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(SampleModelPackage.eNS_URI, theSampleModelPackage);
        return theSampleModelPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getTrajectory() {
        return trajectoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getTrajectory_SamplePath() {
        return (EReference) trajectoryEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getSample() {
        return sampleEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getSample_Reward() {
        return (EReference) sampleEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getSample_Action() {
        return (EReference) sampleEClass.getEStructuralFeatures()
            .get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getSample_PointInTime() {
        return (EAttribute) sampleEClass.getEStructuralFeatures()
            .get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getSample_Current() {
        return (EReference) sampleEClass.getEStructuralFeatures()
            .get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getSample_Next() {
        return (EReference) sampleEClass.getEStructuralFeatures()
            .get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getSample_Observation() {
        return (EReference) sampleEClass.getEStructuralFeatures()
            .get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getSampleModel() {
        return sampleModelEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getSampleModel_Trajectories() {
        return (EReference) sampleModelEClass.getEStructuralFeatures()
            .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public SampleModelFactory getSampleModelFactory() {
        return (SampleModelFactory) getEFactoryInstance();
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
        trajectoryEClass = createEClass(TRAJECTORY);
        createEReference(trajectoryEClass, TRAJECTORY__SAMPLE_PATH);

        sampleEClass = createEClass(SAMPLE);
        createEReference(sampleEClass, SAMPLE__REWARD);
        createEReference(sampleEClass, SAMPLE__ACTION);
        createEAttribute(sampleEClass, SAMPLE__POINT_IN_TIME);
        createEReference(sampleEClass, SAMPLE__CURRENT);
        createEReference(sampleEClass, SAMPLE__NEXT);
        createEReference(sampleEClass, SAMPLE__OBSERVATION);

        sampleModelEClass = createEClass(SAMPLE_MODEL);
        createEReference(sampleModelEClass, SAMPLE_MODEL__TRAJECTORIES);
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

        // Obtain other dependent packages
        MarkovEntityPackage theMarkovEntityPackage = (MarkovEntityPackage) EPackage.Registry.INSTANCE
            .getEPackage(MarkovEntityPackage.eNS_URI);

        // Create type parameters
        ETypeParameter trajectoryEClass_A = addETypeParameter(trajectoryEClass, "A");
        ETypeParameter trajectoryEClass_R = addETypeParameter(trajectoryEClass, "R");
        ETypeParameter sampleEClass_A = addETypeParameter(sampleEClass, "A");
        ETypeParameter sampleEClass_R = addETypeParameter(sampleEClass, "R");
        ETypeParameter sampleModelEClass_A = addETypeParameter(sampleModelEClass, "A");
        ETypeParameter sampleModelEClass_R = addETypeParameter(sampleModelEClass, "R");

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass(trajectoryEClass, Trajectory.class, "Trajectory", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        EGenericType g1 = createEGenericType(this.getSample());
        EGenericType g2 = createEGenericType(trajectoryEClass_A);
        g1.getETypeArguments()
            .add(g2);
        g2 = createEGenericType(trajectoryEClass_R);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getTrajectory_SamplePath(), g1, null, "samplePath", null, 1, -1, Trajectory.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        initEClass(sampleEClass, Sample.class, "Sample", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(theMarkovEntityPackage.getReward());
        g2 = createEGenericType(sampleEClass_R);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getSample_Reward(), g1, null, "reward", null, 0, 1, Sample.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        g1 = createEGenericType(theMarkovEntityPackage.getAction());
        g2 = createEGenericType(sampleEClass_A);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getSample_Action(), g1, null, "action", null, 0, 1, Sample.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSample_PointInTime(), ecorePackage.getEInt(), "pointInTime", null, 1, 1, Sample.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSample_Current(), theMarkovEntityPackage.getState(), null, "current", null, 1, 1,
                Sample.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSample_Next(), theMarkovEntityPackage.getState(), null, "next", null, 1, 1, Sample.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSample_Observation(), theMarkovEntityPackage.getObservation(), null, "observation", null, 0,
                1, Sample.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(sampleModelEClass, SampleModel.class, "SampleModel", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        g1 = createEGenericType(this.getTrajectory());
        g2 = createEGenericType(sampleModelEClass_A);
        g1.getETypeArguments()
            .add(g2);
        g2 = createEGenericType(sampleModelEClass_R);
        g1.getETypeArguments()
            .add(g2);
        initEReference(getSampleModel_Trajectories(), g1, null, "trajectories", null, 1, -1, SampleModel.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
                IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //SampleModelPackageImpl
