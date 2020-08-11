/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage
 * @generated
 */
public interface ReconfigurationparamsFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReconfigurationparamsFactory eINSTANCE = org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.ReconfigurationparamsFactoryImpl
			.init();

	/**
	 * Returns a new object of class '<em>Delta Io TReconfiguration Param Repository</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Delta Io TReconfiguration Param Repository</em>'.
	 * @generated
	 */
	DeltaIoTReconfigurationParamRepository createDeltaIoTReconfigurationParamRepository();

	/**
	 * Returns a new object of class '<em>Distribution Factor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Distribution Factor</em>'.
	 * @generated
	 */
	DistributionFactor createDistributionFactor();

	/**
	 * Returns a new object of class '<em>Distribution Factor Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Distribution Factor Value</em>'.
	 * @generated
	 */
	DistributionFactorValue createDistributionFactorValue();

	/**
	 * Returns a new object of class '<em>Transmission Power</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Transmission Power</em>'.
	 * @generated
	 */
	TransmissionPower createTransmissionPower();

	/**
	 * Returns a new object of class '<em>Transmission Power Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Transmission Power Value</em>'.
	 * @generated
	 */
	TransmissionPowerValue createTransmissionPowerValue();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ReconfigurationparamsPackage getReconfigurationparamsPackage();

} //ReconfigurationparamsFactory
