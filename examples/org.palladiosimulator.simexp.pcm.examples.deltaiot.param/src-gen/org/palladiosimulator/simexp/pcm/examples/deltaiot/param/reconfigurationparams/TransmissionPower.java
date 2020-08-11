/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transmission Power</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getTransmissionPowerValues <em>Transmission Power Values</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getAppliedAssembly <em>Applied Assembly</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getTransmissionPower()
 * @model
 * @generated
 */
public interface TransmissionPower extends EObject {
	/**
	 * Returns the value of the '<em><b>Transmission Power Values</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transmission Power Values</em>' containment reference list.
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getTransmissionPower_TransmissionPowerValues()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<TransmissionPowerValue> getTransmissionPowerValues();

	/**
	 * Returns the value of the '<em><b>Applied Assembly</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Applied Assembly</em>' reference.
	 * @see #setAppliedAssembly(AssemblyContext)
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getTransmissionPower_AppliedAssembly()
	 * @model required="true"
	 * @generated
	 */
	AssemblyContext getAppliedAssembly();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower#getAppliedAssembly <em>Applied Assembly</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Applied Assembly</em>' reference.
	 * @see #getAppliedAssembly()
	 * @generated
	 */
	void setAppliedAssembly(AssemblyContext value);

} // TransmissionPower
