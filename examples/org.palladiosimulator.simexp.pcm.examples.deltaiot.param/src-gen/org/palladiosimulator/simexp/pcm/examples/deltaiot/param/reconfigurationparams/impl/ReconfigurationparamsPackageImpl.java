/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl;

import de.uka.ipd.sdq.identifier.IdentifierPackage;

import de.uka.ipd.sdq.probfunction.ProbfunctionPackage;

import de.uka.ipd.sdq.stoex.StoexPackage;

import de.uka.ipd.sdq.units.UnitsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.palladiosimulator.pcm.PcmPackage;

import org.palladiosimulator.pcm.core.composition.CompositionPackage;

import org.palladiosimulator.pcm.repository.RepositoryPackage;

import org.palladiosimulator.pcm.seff.SeffPackage;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReconfigurationparamsPackageImpl extends EPackageImpl implements ReconfigurationparamsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deltaIoTReconfigurationParamRepositoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass distributionFactorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass distributionFactorValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass transmissionPowerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass transmissionPowerValueEClass = null;

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
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ReconfigurationparamsPackageImpl() {
		super(eNS_URI, ReconfigurationparamsFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ReconfigurationparamsPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ReconfigurationparamsPackage init() {
		if (isInited)
			return (ReconfigurationparamsPackage) EPackage.Registry.INSTANCE
					.getEPackage(ReconfigurationparamsPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredReconfigurationparamsPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		ReconfigurationparamsPackageImpl theReconfigurationparamsPackage = registeredReconfigurationparamsPackage instanceof ReconfigurationparamsPackageImpl
				? (ReconfigurationparamsPackageImpl) registeredReconfigurationparamsPackage
				: new ReconfigurationparamsPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		IdentifierPackage.eINSTANCE.eClass();
		tools.mdsd.modelingfoundations.identifier.IdentifierPackage.eINSTANCE.eClass();
		PcmPackage.eINSTANCE.eClass();
		ProbfunctionPackage.eINSTANCE.eClass();
		StoexPackage.eINSTANCE.eClass();
		UnitsPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theReconfigurationparamsPackage.createPackageContents();

		// Initialize created meta-data
		theReconfigurationparamsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theReconfigurationparamsPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ReconfigurationparamsPackage.eNS_URI, theReconfigurationparamsPackage);
		return theReconfigurationparamsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDeltaIoTReconfigurationParamRepository() {
		return deltaIoTReconfigurationParamRepositoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDeltaIoTReconfigurationParamRepository_DistributionFactors() {
		return (EReference) deltaIoTReconfigurationParamRepositoryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDeltaIoTReconfigurationParamRepository_TransmissionPower() {
		return (EReference) deltaIoTReconfigurationParamRepositoryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDistributionFactor() {
		return distributionFactorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDistributionFactor_FactorValues() {
		return (EReference) distributionFactorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDistributionFactor_AppliedComponent() {
		return (EReference) distributionFactorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDistributionFactorValue() {
		return distributionFactorValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDistributionFactorValue_Value() {
		return (EAttribute) distributionFactorValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDistributionFactorValue_AppliedBranch() {
		return (EReference) distributionFactorValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTransmissionPower() {
		return transmissionPowerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransmissionPower_TransmissionPowerValues() {
		return (EReference) transmissionPowerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransmissionPower_AppliedAssembly() {
		return (EReference) transmissionPowerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTransmissionPowerValue() {
		return transmissionPowerValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransmissionPowerValue_VariableRef() {
		return (EReference) transmissionPowerValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTransmissionPowerValue_PowerSetting() {
		return (EAttribute) transmissionPowerValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReconfigurationparamsFactory getReconfigurationparamsFactory() {
		return (ReconfigurationparamsFactory) getEFactoryInstance();
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
		deltaIoTReconfigurationParamRepositoryEClass = createEClass(DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY);
		createEReference(deltaIoTReconfigurationParamRepositoryEClass,
				DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS);
		createEReference(deltaIoTReconfigurationParamRepositoryEClass,
				DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER);

		distributionFactorEClass = createEClass(DISTRIBUTION_FACTOR);
		createEReference(distributionFactorEClass, DISTRIBUTION_FACTOR__FACTOR_VALUES);
		createEReference(distributionFactorEClass, DISTRIBUTION_FACTOR__APPLIED_COMPONENT);

		distributionFactorValueEClass = createEClass(DISTRIBUTION_FACTOR_VALUE);
		createEAttribute(distributionFactorValueEClass, DISTRIBUTION_FACTOR_VALUE__VALUE);
		createEReference(distributionFactorValueEClass, DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH);

		transmissionPowerEClass = createEClass(TRANSMISSION_POWER);
		createEReference(transmissionPowerEClass, TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES);
		createEReference(transmissionPowerEClass, TRANSMISSION_POWER__APPLIED_ASSEMBLY);

		transmissionPowerValueEClass = createEClass(TRANSMISSION_POWER_VALUE);
		createEReference(transmissionPowerValueEClass, TRANSMISSION_POWER_VALUE__VARIABLE_REF);
		createEAttribute(transmissionPowerValueEClass, TRANSMISSION_POWER_VALUE__POWER_SETTING);
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
		tools.mdsd.modelingfoundations.identifier.IdentifierPackage theIdentifierPackage_1 = (tools.mdsd.modelingfoundations.identifier.IdentifierPackage) EPackage.Registry.INSTANCE
				.getEPackage(tools.mdsd.modelingfoundations.identifier.IdentifierPackage.eNS_URI);
		RepositoryPackage theRepositoryPackage = (RepositoryPackage) EPackage.Registry.INSTANCE
				.getEPackage(RepositoryPackage.eNS_URI);
		SeffPackage theSeffPackage = (SeffPackage) EPackage.Registry.INSTANCE.getEPackage(SeffPackage.eNS_URI);
		CompositionPackage theCompositionPackage = (CompositionPackage) EPackage.Registry.INSTANCE
				.getEPackage(CompositionPackage.eNS_URI);
		StoexPackage theStoexPackage = (StoexPackage) EPackage.Registry.INSTANCE.getEPackage(StoexPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		deltaIoTReconfigurationParamRepositoryEClass.getESuperTypes().add(theIdentifierPackage_1.getNamedElement());
		distributionFactorEClass.getESuperTypes().add(theIdentifierPackage_1.getNamedElement());

		// Initialize classes, features, and operations; add parameters
		initEClass(deltaIoTReconfigurationParamRepositoryEClass, DeltaIoTReconfigurationParamRepository.class,
				"DeltaIoTReconfigurationParamRepository", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDeltaIoTReconfigurationParamRepository_DistributionFactors(), this.getDistributionFactor(),
				null, "distributionFactors", null, 0, -1, DeltaIoTReconfigurationParamRepository.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEReference(getDeltaIoTReconfigurationParamRepository_TransmissionPower(), this.getTransmissionPower(), null,
				"transmissionPower", null, 0, -1, DeltaIoTReconfigurationParamRepository.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);

		initEClass(distributionFactorEClass, DistributionFactor.class, "DistributionFactor", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDistributionFactor_FactorValues(), this.getDistributionFactorValue(), null, "factorValues",
				null, 2, -1, DistributionFactor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDistributionFactor_AppliedComponent(), theRepositoryPackage.getRepositoryComponent(), null,
				"appliedComponent", null, 1, 1, DistributionFactor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(distributionFactorValueEClass, DistributionFactorValue.class, "DistributionFactorValue",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDistributionFactorValue_Value(), ecorePackage.getEDouble(), "value", null, 1, 1,
				DistributionFactorValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDistributionFactorValue_AppliedBranch(), theSeffPackage.getProbabilisticBranchTransition(),
				null, "appliedBranch", null, 1, 1, DistributionFactorValue.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(transmissionPowerEClass, TransmissionPower.class, "TransmissionPower", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTransmissionPower_TransmissionPowerValues(), this.getTransmissionPowerValue(), null,
				"transmissionPowerValues", null, 1, -1, TransmissionPower.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTransmissionPower_AppliedAssembly(), theCompositionPackage.getAssemblyContext(), null,
				"appliedAssembly", null, 1, 1, TransmissionPower.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(transmissionPowerValueEClass, TransmissionPowerValue.class, "TransmissionPowerValue", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTransmissionPowerValue_VariableRef(), theStoexPackage.getVariableReference(), null,
				"variableRef", null, 1, 1, TransmissionPowerValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTransmissionPowerValue_PowerSetting(), ecorePackage.getEInt(), "powerSetting", null, 1, 1,
				TransmissionPowerValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //ReconfigurationparamsPackageImpl
