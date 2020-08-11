/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams;

import de.uka.ipd.sdq.stoex.VariableReference;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transmission Power Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getVariableRef <em>Variable Ref</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getPowerSetting <em>Power Setting</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getTransmissionPowerValue()
 * @model
 * @generated
 */
public interface TransmissionPowerValue extends EObject {
	/**
	 * Returns the value of the '<em><b>Variable Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variable Ref</em>' reference.
	 * @see #setVariableRef(VariableReference)
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getTransmissionPowerValue_VariableRef()
	 * @model required="true"
	 * @generated
	 */
	VariableReference getVariableRef();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getVariableRef <em>Variable Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Variable Ref</em>' reference.
	 * @see #getVariableRef()
	 * @generated
	 */
	void setVariableRef(VariableReference value);

	/**
	 * Returns the value of the '<em><b>Power Setting</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Power Setting</em>' attribute.
	 * @see #setPowerSetting(int)
	 * @see org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage#getTransmissionPowerValue_PowerSetting()
	 * @model required="true"
	 * @generated
	 */
	int getPowerSetting();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue#getPowerSetting <em>Power Setting</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Power Setting</em>' attribute.
	 * @see #getPowerSetting()
	 * @generated
	 */
	void setPowerSetting(int value);

} // TransmissionPowerValue
