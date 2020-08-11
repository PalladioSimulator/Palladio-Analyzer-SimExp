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

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.pcm.repository.RepositoryComponent;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;

import tools.mdsd.modelingfoundations.identifier.impl.NamedElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Distribution Factor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorImpl#getFactorValues <em>Factor Values</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DistributionFactorImpl#getAppliedComponent <em>Applied Component</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DistributionFactorImpl extends NamedElementImpl implements DistributionFactor {
	/**
	 * The cached value of the '{@link #getFactorValues() <em>Factor Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFactorValues()
	 * @generated
	 * @ordered
	 */
	protected EList<DistributionFactorValue> factorValues;

	/**
	 * The cached value of the '{@link #getAppliedComponent() <em>Applied Component</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAppliedComponent()
	 * @generated
	 * @ordered
	 */
	protected RepositoryComponent appliedComponent;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DistributionFactorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReconfigurationparamsPackage.Literals.DISTRIBUTION_FACTOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DistributionFactorValue> getFactorValues() {
		if (factorValues == null) {
			factorValues = new EObjectContainmentEList<DistributionFactorValue>(DistributionFactorValue.class, this,
					ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__FACTOR_VALUES);
		}
		return factorValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RepositoryComponent getAppliedComponent() {
		if (appliedComponent != null && ((EObject) appliedComponent).eIsProxy()) {
			InternalEObject oldAppliedComponent = (InternalEObject) appliedComponent;
			appliedComponent = (RepositoryComponent) eResolveProxy(oldAppliedComponent);
			if (appliedComponent != oldAppliedComponent) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__APPLIED_COMPONENT, oldAppliedComponent,
							appliedComponent));
			}
		}
		return appliedComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RepositoryComponent basicGetAppliedComponent() {
		return appliedComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAppliedComponent(RepositoryComponent newAppliedComponent) {
		RepositoryComponent oldAppliedComponent = appliedComponent;
		appliedComponent = newAppliedComponent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__APPLIED_COMPONENT, oldAppliedComponent,
					appliedComponent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__FACTOR_VALUES:
			return ((InternalEList<?>) getFactorValues()).basicRemove(otherEnd, msgs);
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__FACTOR_VALUES:
			return getFactorValues();
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__APPLIED_COMPONENT:
			if (resolve)
				return getAppliedComponent();
			return basicGetAppliedComponent();
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__FACTOR_VALUES:
			getFactorValues().clear();
			getFactorValues().addAll((Collection<? extends DistributionFactorValue>) newValue);
			return;
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__APPLIED_COMPONENT:
			setAppliedComponent((RepositoryComponent) newValue);
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__FACTOR_VALUES:
			getFactorValues().clear();
			return;
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__APPLIED_COMPONENT:
			setAppliedComponent((RepositoryComponent) null);
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
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__FACTOR_VALUES:
			return factorValues != null && !factorValues.isEmpty();
		case ReconfigurationparamsPackage.DISTRIBUTION_FACTOR__APPLIED_COMPONENT:
			return appliedComponent != null;
		}
		return super.eIsSet(featureID);
	}

} //DistributionFactorImpl
