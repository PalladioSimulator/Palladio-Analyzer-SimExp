/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReconfigurationparamsFactoryImpl extends EFactoryImpl implements ReconfigurationparamsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ReconfigurationparamsFactory init() {
		try {
			ReconfigurationparamsFactory theReconfigurationparamsFactory = (ReconfigurationparamsFactory) EPackage.Registry.INSTANCE
					.getEFactory(ReconfigurationparamsPackage.eNS_URI);
			if (theReconfigurationparamsFactory != null) {
				return theReconfigurationparamsFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ReconfigurationparamsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReconfigurationparamsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY:
			return createDeltaIoTReconfigurationParamRepository();
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR:
			return createDistributionFactor();
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE:
			return createDistributionFactorValue();
		case ReconfigurationparamsPackage.TRANSMISSION_POWER:
			return createTransmissionPower();
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE:
			return createTransmissionPowerValue();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeltaIoTReconfigurationParamRepository createDeltaIoTReconfigurationParamRepository() {
		DeltaIoTReconfigurationParamRepositoryImpl deltaIoTReconfigurationParamRepository = new DeltaIoTReconfigurationParamRepositoryImpl();
		return deltaIoTReconfigurationParamRepository;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DistributionFactor createDistributionFactor() {
		DistributionFactorImpl distributionFactor = new DistributionFactorImpl();
		return distributionFactor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DistributionFactorValue createDistributionFactorValue() {
		DistributionFactorValueImpl distributionFactorValue = new DistributionFactorValueImpl();
		return distributionFactorValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TransmissionPower createTransmissionPower() {
		TransmissionPowerImpl transmissionPower = new TransmissionPowerImpl();
		return transmissionPower;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TransmissionPowerValue createTransmissionPowerValue() {
		TransmissionPowerValueImpl transmissionPowerValue = new TransmissionPowerValueImpl();
		return transmissionPowerValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReconfigurationparamsPackage getReconfigurationparamsPackage() {
		return (ReconfigurationparamsPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ReconfigurationparamsPackage getPackage() {
		return ReconfigurationparamsPackage.eINSTANCE;
	}

} //ReconfigurationparamsFactoryImpl
