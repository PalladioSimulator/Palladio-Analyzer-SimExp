/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import org.eclipse.emf.common.util.EList;

import tools.mdsd.modelingfoundations.identifier.NamedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Delta Io TReconfiguration Param Repository</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository#getDistributionFactors <em>Distribution Factors</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository#getTransmissionPower <em>Transmission Power</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDeltaIoTReconfigurationParamRepository()
 * @model
 * @generated
 */
public interface DeltaIoTReconfigurationParamRepository extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Distribution Factors</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Distribution Factors</em>' containment reference list.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDeltaIoTReconfigurationParamRepository_DistributionFactors()
	 * @model containment="true"
	 * @generated
	 */
	EList<DistributionFactor> getDistributionFactors();

	/**
	 * Returns the value of the '<em><b>Transmission Power</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transmission Power</em>' containment reference list.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getDeltaIoTReconfigurationParamRepository_TransmissionPower()
	 * @model containment="true"
	 * @generated
	 */
	EList<TransmissionPower> getTransmissionPower();

} // DeltaIoTReconfigurationParamRepository
