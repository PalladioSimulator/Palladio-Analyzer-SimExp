/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Distribution Factor Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorValueImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorValueImpl#getAppliedBranch <em>Applied Branch</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DistributionFactorValueImpl extends MinimalEObjectImpl.Container implements DistributionFactorValue {
	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final double VALUE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected double value = VALUE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAppliedBranch() <em>Applied Branch</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAppliedBranch()
	 * @generated
	 * @ordered
	 */
	protected ProbabilisticBranchTransition appliedBranch;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DistributionFactorValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReconfigurationparamsPackage.Literals.DISTRIBUTION_FACTOR_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValue(double newValue) {
		double oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProbabilisticBranchTransition getAppliedBranch() {
		if (appliedBranch != null && ((EObject) appliedBranch).eIsProxy()) {
			InternalEObject oldAppliedBranch = (InternalEObject) appliedBranch;
			appliedBranch = (ProbabilisticBranchTransition) eResolveProxy(oldAppliedBranch);
			if (appliedBranch != oldAppliedBranch) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH, oldAppliedBranch,
							appliedBranch));
			}
		}
		return appliedBranch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProbabilisticBranchTransition basicGetAppliedBranch() {
		return appliedBranch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAppliedBranch(ProbabilisticBranchTransition newAppliedBranch) {
		ProbabilisticBranchTransition oldAppliedBranch = appliedBranch;
		appliedBranch = newAppliedBranch;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH, oldAppliedBranch,
					appliedBranch));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__VALUE:
			return getValue();
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH:
			if (resolve)
				return getAppliedBranch();
			return basicGetAppliedBranch();
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__VALUE:
			setValue((Double) newValue);
			return;
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH:
			setAppliedBranch((ProbabilisticBranchTransition) newValue);
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__VALUE:
			setValue(VALUE_EDEFAULT);
			return;
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH:
			setAppliedBranch((ProbabilisticBranchTransition) null);
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__VALUE:
			return value != VALUE_EDEFAULT;
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR_VALUE__APPLIED_BRANCH:
			return appliedBranch != null;
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
		result.append(" (value: ");
		result.append(value);
		result.append(')');
		return result.toString();
	}

} //DistributionFactorValueImpl
