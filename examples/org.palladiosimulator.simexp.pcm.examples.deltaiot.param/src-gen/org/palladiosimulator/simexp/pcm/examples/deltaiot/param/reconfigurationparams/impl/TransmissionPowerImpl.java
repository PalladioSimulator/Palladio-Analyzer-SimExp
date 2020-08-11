/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transmission Power</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerImpl#getTransmissionPowerValues <em>Transmission Power Values</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.TransmissionPowerImpl#getAppliedAssembly <em>Applied Assembly</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransmissionPowerImpl extends MinimalEObjectImpl.Container implements TransmissionPower {
	/**
	 * The cached value of the '{@link #getTransmissionPowerValues() <em>Transmission Power Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransmissionPowerValues()
	 * @generated
	 * @ordered
	 */
	protected EList<TransmissionPowerValue> transmissionPowerValues;

	/**
	 * The cached value of the '{@link #getAppliedAssembly() <em>Applied Assembly</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAppliedAssembly()
	 * @generated
	 * @ordered
	 */
	protected AssemblyContext appliedAssembly;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TransmissionPowerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReconfigurationparamsPackage.Literals.TRANSMISSION_POWER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<TransmissionPowerValue> getTransmissionPowerValues() {
		if (transmissionPowerValues == null) {
			transmissionPowerValues = new EObjectContainmentEList<TransmissionPowerValue>(TransmissionPowerValue.class,
					this, ReconfigurationparamsPackage.TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES);
		}
		return transmissionPowerValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AssemblyContext getAppliedAssembly() {
		if (appliedAssembly != null && ((EObject) appliedAssembly).eIsProxy()) {
			InternalEObject oldAppliedAssembly = (InternalEObject) appliedAssembly;
			appliedAssembly = (AssemblyContext) eResolveProxy(oldAppliedAssembly);
			if (appliedAssembly != oldAppliedAssembly) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ReconfigurationparamsPackage.TRANSMISSION_POWER__APPLIED_ASSEMBLY, oldAppliedAssembly,
							appliedAssembly));
			}
		}
		return appliedAssembly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssemblyContext basicGetAppliedAssembly() {
		return appliedAssembly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAppliedAssembly(AssemblyContext newAppliedAssembly) {
		AssemblyContext oldAppliedAssembly = appliedAssembly;
		appliedAssembly = newAppliedAssembly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ReconfigurationparamsPackage.TRANSMISSION_POWER__APPLIED_ASSEMBLY, oldAppliedAssembly,
					appliedAssembly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES:
			return ((InternalEList<?>) getTransmissionPowerValues()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES:
			return getTransmissionPowerValues();
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__APPLIED_ASSEMBLY:
			if (resolve)
				return getAppliedAssembly();
			return basicGetAppliedAssembly();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES:
			getTransmissionPowerValues().clear();
			getTransmissionPowerValues().addAll((Collection<? extends TransmissionPowerValue>) newValue);
			return;
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__APPLIED_ASSEMBLY:
			setAppliedAssembly((AssemblyContext) newValue);
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
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES:
			getTransmissionPowerValues().clear();
			return;
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__APPLIED_ASSEMBLY:
			setAppliedAssembly((AssemblyContext) null);
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
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__TRANSMISSION_POWER_VALUES:
			return transmissionPowerValues != null && !transmissionPowerValues.isEmpty();
		case ReconfigurationparamsPackage.TRANSMISSION_POWER__APPLIED_ASSEMBLY:
			return appliedAssembly != null;
		}
		return super.eIsSet(featureID);
	}

} //TransmissionPowerImpl
