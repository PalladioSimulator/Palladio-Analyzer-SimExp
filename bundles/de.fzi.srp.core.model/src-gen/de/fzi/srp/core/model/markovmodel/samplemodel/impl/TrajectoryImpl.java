/**
 */
package de.fzi.srp.core.model.markovmodel.samplemodel.impl;

import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;
import de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage;
import de.fzi.srp.core.model.markovmodel.samplemodel.Trajectory;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trajectory</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.samplemodel.impl.TrajectoryImpl#getSamplePath <em>Sample Path</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TrajectoryImpl extends MinimalEObjectImpl.Container implements Trajectory {
	/**
	 * The cached value of the '{@link #getSamplePath() <em>Sample Path</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSamplePath()
	 * @generated
	 * @ordered
	 */
	protected EList<Sample> samplePath;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TrajectoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SampleModelPackage.Literals.TRAJECTORY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Sample> getSamplePath() {
		if (samplePath == null) {
			samplePath = new EObjectContainmentEList<Sample>(Sample.class, this,
					SampleModelPackage.TRAJECTORY__SAMPLE_PATH);
		}
		return samplePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case SampleModelPackage.TRAJECTORY__SAMPLE_PATH:
			return ((InternalEList<?>) getSamplePath()).basicRemove(otherEnd, msgs);
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
		case SampleModelPackage.TRAJECTORY__SAMPLE_PATH:
			return getSamplePath();
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
		case SampleModelPackage.TRAJECTORY__SAMPLE_PATH:
			getSamplePath().clear();
			getSamplePath().addAll((Collection<? extends Sample>) newValue);
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
		case SampleModelPackage.TRAJECTORY__SAMPLE_PATH:
			getSamplePath().clear();
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
		case SampleModelPackage.TRAJECTORY__SAMPLE_PATH:
			return samplePath != null && !samplePath.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //TrajectoryImpl
