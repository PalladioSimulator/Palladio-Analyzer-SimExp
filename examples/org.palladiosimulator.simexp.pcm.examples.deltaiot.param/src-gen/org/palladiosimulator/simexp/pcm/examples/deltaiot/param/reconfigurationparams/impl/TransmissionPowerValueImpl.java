/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl;

import de.uka.ipd.sdq.stoex.VariableReference;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transmission Power Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerValueImpl#getVariableRef <em>Variable Ref</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerValueImpl#getPowerSetting <em>Power Setting</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransmissionPowerValueImpl extends MinimalEObjectImpl.Container implements TransmissionPowerValue {
	/**
	 * The cached value of the '{@link #getVariableRef() <em>Variable Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariableRef()
	 * @generated
	 * @ordered
	 */
	protected VariableReference variableRef;

	/**
	 * The default value of the '{@link #getPowerSetting() <em>Power Setting</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPowerSetting()
	 * @generated
	 * @ordered
	 */
	protected static final int POWER_SETTING_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPowerSetting() <em>Power Setting</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPowerSetting()
	 * @generated
	 * @ordered
	 */
	protected int powerSetting = POWER_SETTING_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TransmissionPowerValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReconfigurationparamsPackage.Literals.TRANSMISSION_POWER_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public VariableReference getVariableRef() {
		if (variableRef != null && ((EObject) variableRef).eIsProxy()) {
			InternalEObject oldVariableRef = (InternalEObject) variableRef;
			variableRef = (VariableReference) eResolveProxy(oldVariableRef);
			if (variableRef != oldVariableRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__VARIABLE_REF, oldVariableRef,
							variableRef));
			}
		}
		return variableRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VariableReference basicGetVariableRef() {
		return variableRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVariableRef(VariableReference newVariableRef) {
		VariableReference oldVariableRef = variableRef;
		variableRef = newVariableRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__VARIABLE_REF, oldVariableRef, variableRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getPowerSetting() {
		return powerSetting;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPowerSetting(int newPowerSetting) {
		int oldPowerSetting = powerSetting;
		powerSetting = newPowerSetting;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__POWER_SETTING, oldPowerSetting,
					powerSetting));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__VARIABLE_REF:
			if (resolve)
				return getVariableRef();
			return basicGetVariableRef();
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__POWER_SETTING:
			return getPowerSetting();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__VARIABLE_REF:
			setVariableRef((VariableReference) newValue);
			return;
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__POWER_SETTING:
			setPowerSetting((Integer) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__VARIABLE_REF:
			setVariableRef((VariableReference) null);
			return;
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__POWER_SETTING:
			setPowerSetting(POWER_SETTING_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__VARIABLE_REF:
			return variableRef != null;
		case ReconfigurationparamsPackage.TRANSMISSION_POWER_VALUE__POWER_SETTING:
			return powerSetting != POWER_SETTING_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (powerSetting: ");
		result.append(powerSetting);
		result.append(')');
		return result.toString();
	}

} //TransmissionPowerValueImpl
