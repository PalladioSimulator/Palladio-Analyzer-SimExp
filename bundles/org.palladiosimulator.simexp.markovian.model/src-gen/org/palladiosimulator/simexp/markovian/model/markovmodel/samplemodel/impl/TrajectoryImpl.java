/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trajectory</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.TrajectoryImpl#getSamplePath <em>Sample Path</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TrajectoryImpl<S, A, R> extends MinimalEObjectImpl.Container implements Trajectory<S, A, R> {
    /**
     * The cached value of the '{@link #getSamplePath() <em>Sample Path</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSamplePath()
     * @generated
     * @ordered
     */
    protected EList<Sample<S, A, R>> samplePath;

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
    @Override
    public EList<Sample<S, A, R>> getSamplePath() {
        if (samplePath == null) {
            samplePath = new EObjectContainmentEList<Sample<S, A, R>>(Sample.class, this,
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
            getSamplePath().addAll((Collection<? extends Sample<S, A, R>>) newValue);
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
