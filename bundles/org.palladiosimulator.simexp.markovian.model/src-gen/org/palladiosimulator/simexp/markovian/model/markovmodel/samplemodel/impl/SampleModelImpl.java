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

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sample Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelImpl#getTrajectories <em>Trajectories</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SampleModelImpl<S, A, R> extends MinimalEObjectImpl.Container implements SampleModel<S, A, R> {
    /**
     * The cached value of the '{@link #getTrajectories() <em>Trajectories</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTrajectories()
     * @generated
     * @ordered
     */
    protected EList<Trajectory<S, A, R>> trajectories;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SampleModelImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SampleModelPackage.Literals.SAMPLE_MODEL;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Trajectory<S, A, R>> getTrajectories() {
        if (trajectories == null) {
            trajectories = new EObjectContainmentEList<Trajectory<S, A, R>>(Trajectory.class, this,
                    SampleModelPackage.SAMPLE_MODEL__TRAJECTORIES);
        }
        return trajectories;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case SampleModelPackage.SAMPLE_MODEL__TRAJECTORIES:
            return ((InternalEList<?>) getTrajectories()).basicRemove(otherEnd, msgs);
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
        case SampleModelPackage.SAMPLE_MODEL__TRAJECTORIES:
            return getTrajectories();
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
        case SampleModelPackage.SAMPLE_MODEL__TRAJECTORIES:
            getTrajectories().clear();
            getTrajectories().addAll((Collection<? extends Trajectory<S, A, R>>) newValue);
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
        case SampleModelPackage.SAMPLE_MODEL__TRAJECTORIES:
            getTrajectories().clear();
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
        case SampleModelPackage.SAMPLE_MODEL__TRAJECTORIES:
            return trajectories != null && !trajectories.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //SampleModelImpl
