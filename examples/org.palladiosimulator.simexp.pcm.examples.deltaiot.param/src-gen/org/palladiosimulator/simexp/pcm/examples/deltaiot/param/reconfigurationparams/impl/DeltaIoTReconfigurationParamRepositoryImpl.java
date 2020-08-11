/**
 */
package org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.ReconfigurationparamsPackage;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;

import tools.mdsd.modelingfoundations.identifier.impl.NamedElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Delta Io TReconfiguration Param Repository</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DeltaIoTReconfigurationParamRepositoryImpl#getDistributionFactors <em>Distribution Factors</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.impl.DeltaIoTReconfigurationParamRepositoryImpl#getTransmissionPower <em>Transmission Power</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DeltaIoTReconfigurationParamRepositoryImpl extends NamedElementImpl
		implements DeltaIoTReconfigurationParamRepository {
	/**
	 * The cached value of the '{@link #getDistributionFactors() <em>Distribution Factors</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDistributionFactors()
	 * @generated
	 * @ordered
	 */
	protected EList<DistributionFactor> distributionFactors;

	/**
	 * The cached value of the '{@link #getTransmissionPower() <em>Transmission Power</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransmissionPower()
	 * @generated
	 * @ordered
	 */
	protected EList<TransmissionPower> transmissionPower;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeltaIoTReconfigurationParamRepositoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReconfigurationparamsPackage.Literals.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DistributionFactor> getDistributionFactors() {
		if (distributionFactors == null) {
			distributionFactors = new EObjectContainmentEList<DistributionFactor>(DistributionFactor.class, this,
					ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS);
		}
		return distributionFactors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<TransmissionPower> getTransmissionPower() {
		if (transmissionPower == null) {
			transmissionPower = new EObjectContainmentEList<TransmissionPower>(TransmissionPower.class, this,
					ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER);
		}
		return transmissionPower;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS:
			return ((InternalEList<?>) getDistributionFactors()).basicRemove(otherEnd, msgs);
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER:
			return ((InternalEList<?>) getTransmissionPower()).basicRemove(otherEnd, msgs);
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
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS:
			return getDistributionFactors();
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER:
			return getTransmissionPower();
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
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS:
			getDistributionFactors().clear();
			getDistributionFactors().addAll((Collection<? extends DistributionFactor>) newValue);
			return;
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER:
			getTransmissionPower().clear();
			getTransmissionPower().addAll((Collection<? extends TransmissionPower>) newValue);
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
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS:
			getDistributionFactors().clear();
			return;
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER:
			getTransmissionPower().clear();
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
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__DISTRIBUTION_FACTORS:
			return distributionFactors != null && !distributionFactors.isEmpty();
		case ReconfigurationparamsPackage.DELTA_IO_TRECONFIGURATION_PARAM_REPOSITORY__TRANSMISSION_POWER:
			return transmissionPower != null && !transmissionPower.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //DeltaIoTReconfigurationParamRepositoryImpl
