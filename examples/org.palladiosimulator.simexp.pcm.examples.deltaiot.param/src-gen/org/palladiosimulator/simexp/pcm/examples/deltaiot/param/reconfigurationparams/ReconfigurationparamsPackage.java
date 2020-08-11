/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import tools.mdsd.modelingfoundations.identifier.IdentifierPackage;

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
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsFactory
 * @model kind="package"
 * @generated
 */
public interface ReconfigurationparamsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "reconfigurationparams";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://palladiosimulator.org/reconfigurationparams/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "reconfigurationparams";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReconfigurationparamsPackage eINSTANCE = org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DeltaIoTReconfigurationParamRepositoryImpl <em>Delta Io TReconfiguration Param Repository</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DeltaIoTReconfigurationParamRepositoryImpl
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getDeltaIoTReconfigurationParamRepository()
	 * @generated
	 */
	int DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY = 0;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__ENTITY_NAME = IdentifierPackage.NAMED_ELEMENT__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Distribution Factors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS = IdentifierPackage.NAMED_ELEMENT_FEATURE_COUNT
			+ 0;

	/**
	 * The feature id for the '<em><b>Transmission Power</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER = IdentifierPackage.NAMED_ELEMENT_FEATURE_COUNT
			+ 1;

	/**
	 * The number of structural features of the '<em>Delta Io TReconfiguration Param Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY_FEATURE_COUNT = IdentifierPackage.NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Delta Io TReconfiguration Param Repository</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY_OPERATION_COUNT = IdentifierPackage.NAMED_ELEMENT_OPERATION_COUNT
			+ 0;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorImpl <em>Distribution Factor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorImpl
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getDistributionFactor()
	 * @generated
	 */
	int DISTRIBUTION_FACTOR = 1;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR__ENTITY_NAME = IdentifierPackage.NAMED_ELEMENT__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Factor Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR__FACTOR_VALUES = IdentifierPackage.NAMED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Applied Component</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR__APPLIED_COMPONENT = IdentifierPackage.NAMED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Distribution Factor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR_FEATURE_COUNT = IdentifierPackage.NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Distribution Factor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR_OPERATION_COUNT = IdentifierPackage.NAMED_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorValueImpl <em>Distribution Factor Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorValueImpl
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getDistributionFactorValue()
	 * @generated
	 */
	int DISTRIBUTION_FACTOR_VALUE = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR_VALUE__VALUE = 0;

	/**
	 * The feature id for the '<em><b>Applied Branch</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH = 1;

	/**
	 * The number of structural features of the '<em>Distribution Factor Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR_VALUE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Distribution Factor Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_FACTOR_VALUE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerImpl <em>Transmission Power</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerImpl
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getTransmissionPower()
	 * @generated
	 */
	int TRANSMISSION_POWER = 3;

	/**
	 * The feature id for the '<em><b>Transmission Power Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES = 0;

	/**
	 * The feature id for the '<em><b>Applied Assembly</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER__APPLIED_ASSEMBLY = 1;

	/**
	 * The number of structural features of the '<em>Transmission Power</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Transmission Power</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerValueImpl <em>Transmission Power Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerValueImpl
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getTransmissionPowerValue()
	 * @generated
	 */
	int TRANSMISSION_POWER_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Variable Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER_VALUE__VARIABLE_REF = 0;

	/**
	 * The feature id for the '<em><b>Power Setting</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER_VALUE__POWER_SETTING = 1;

	/**
	 * The number of structural features of the '<em>Transmission Power Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER_VALUE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Transmission Power Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSMISSION_POWER_VALUE_OPERATION_COUNT = 0;

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository <em>Delta Io TReconfiguration Param Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Delta Io TReconfiguration Param Repository</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository
	 * @generated
	 */
	EClass getDeltaIoTReconfigurationParamRepository();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository#getDistributionFactors <em>Distribution Factors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Distribution Factors</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository#getDistributionFactors()
	 * @see #getDeltaIoTReconfigurationParamRepository()
	 * @generated
	 */
	EReference getDeltaIoTReconfigurationParamRepository_DistributionFactors();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository#getTransmissionPower <em>Transmission Power</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Transmission Power</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository#getTransmissionPower()
	 * @see #getDeltaIoTReconfigurationParamRepository()
	 * @generated
	 */
	EReference getDeltaIoTReconfigurationParamRepository_TransmissionPower();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor <em>Distribution Factor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Distribution Factor</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor
	 * @generated
	 */
	EClass getDistributionFactor();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getFactorValues <em>Factor Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Factor Values</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getFactorValues()
	 * @see #getDistributionFactor()
	 * @generated
	 */
	EReference getDistributionFactor_FactorValues();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getAppliedComponent <em>Applied Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Applied Component</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor#getAppliedComponent()
	 * @see #getDistributionFactor()
	 * @generated
	 */
	EReference getDistributionFactor_AppliedComponent();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue <em>Distribution Factor Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Distribution Factor Value</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue
	 * @generated
	 */
	EClass getDistributionFactorValue();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getValue()
	 * @see #getDistributionFactorValue()
	 * @generated
	 */
	EAttribute getDistributionFactorValue_Value();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getAppliedBranch <em>Applied Branch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Applied Branch</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue#getAppliedBranch()
	 * @see #getDistributionFactorValue()
	 * @generated
	 */
	EReference getDistributionFactorValue_AppliedBranch();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower <em>Transmission Power</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Transmission Power</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower
	 * @generated
	 */
	EClass getTransmissionPower();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getTransmissionPowerValues <em>Transmission Power Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Transmission Power Values</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getTransmissionPowerValues()
	 * @see #getTransmissionPower()
	 * @generated
	 */
	EReference getTransmissionPower_TransmissionPowerValues();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getAppliedAssembly <em>Applied Assembly</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Applied Assembly</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getAppliedAssembly()
	 * @see #getTransmissionPower()
	 * @generated
	 */
	EReference getTransmissionPower_AppliedAssembly();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue <em>Transmission Power Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Transmission Power Value</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue
	 * @generated
	 */
	EClass getTransmissionPowerValue();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getVariableRef <em>Variable Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Variable Ref</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getVariableRef()
	 * @see #getTransmissionPowerValue()
	 * @generated
	 */
	EReference getTransmissionPowerValue_VariableRef();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getPowerSetting <em>Power Setting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Power Setting</em>'.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getPowerSetting()
	 * @see #getTransmissionPowerValue()
	 * @generated
	 */
	EAttribute getTransmissionPowerValue_PowerSetting();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ReconfigurationparamsFactory getReconfigurationparamsFactory();

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
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DeltaIoTReconfigurationParamRepositoryImpl <em>Delta Io TReconfiguration Param Repository</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DeltaIoTReconfigurationParamRepositoryImpl
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getDeltaIoTReconfigurationParamRepository()
		 * @generated
		 */
		EClass DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY = eINSTANCE.getDeltaIoTReconfigurationParamRepository();

		/**
		 * The meta object literal for the '<em><b>Distribution Factors</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS = eINSTANCE
				.getDeltaIoTReconfigurationParamRepository_DistributionFactors();

		/**
		 * The meta object literal for the '<em><b>Transmission Power</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER = eINSTANCE
				.getDeltaIoTReconfigurationParamRepository_TransmissionPower();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorImpl <em>Distribution Factor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorImpl
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getDistributionFactor()
		 * @generated
		 */
		EClass DISTRIBUTION_FACTOR = eINSTANCE.getDistributionFactor();

		/**
		 * The meta object literal for the '<em><b>Factor Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DISTRIBUTION_FACTOR__FACTOR_VALUES = eINSTANCE.getDistributionFactor_FactorValues();

		/**
		 * The meta object literal for the '<em><b>Applied Component</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DISTRIBUTION_FACTOR__APPLIED_COMPONENT = eINSTANCE.getDistributionFactor_AppliedComponent();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorValueImpl <em>Distribution Factor Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorValueImpl
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getDistributionFactorValue()
		 * @generated
		 */
		EClass DISTRIBUTION_FACTOR_VALUE = eINSTANCE.getDistributionFactorValue();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DISTRIBUTION_FACTOR_VALUE__VALUE = eINSTANCE.getDistributionFactorValue_Value();

		/**
		 * The meta object literal for the '<em><b>Applied Branch</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH = eINSTANCE.getDistributionFactorValue_AppliedBranch();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerImpl <em>Transmission Power</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerImpl
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getTransmissionPower()
		 * @generated
		 */
		EClass TRANSMISSION_POWER = eINSTANCE.getTransmissionPower();

		/**
		 * The meta object literal for the '<em><b>Transmission Power Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES = eINSTANCE
				.getTransmissionPower_TransmissionPowerValues();

		/**
		 * The meta object literal for the '<em><b>Applied Assembly</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSMISSION_POWER__APPLIED_ASSEMBLY = eINSTANCE.getTransmissionPower_AppliedAssembly();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerValueImpl <em>Transmission Power Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerValueImpl
		 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsPackageImpl#getTransmissionPowerValue()
		 * @generated
		 */
		EClass TRANSMISSION_POWER_VALUE = eINSTANCE.getTransmissionPowerValue();

		/**
		 * The meta object literal for the '<em><b>Variable Ref</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSMISSION_POWER_VALUE__VARIABLE_REF = eINSTANCE.getTransmissionPowerValue_VariableRef();

		/**
		 * The meta object literal for the '<em><b>Power Setting</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSMISSION_POWER_VALUE__POWER_SETTING = eINSTANCE.getTransmissionPowerValue_PowerSetting();

	}

} //ReconfigurationparamsPackage
