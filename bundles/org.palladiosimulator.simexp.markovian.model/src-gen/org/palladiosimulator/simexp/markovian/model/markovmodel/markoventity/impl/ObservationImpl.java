/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Observation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl#getObserved <em>Observed</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ObservationImpl extends MinimalEObjectImpl.Container implements Observation {
    /**
     * The cached value of the '{@link #getObserved() <em>Observed</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getObserved()
     * @generated
     * @ordered
     */
    protected State observed;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ObservationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MarkovEntityPackage.Literals.OBSERVATION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public State getObserved() {
        if (observed != null && observed.eIsProxy()) {
            InternalEObject oldObserved = (InternalEObject) observed;
            observed = (State) eResolveProxy(oldObserved);
            if (observed != oldObserved) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, MarkovEntityPackage.OBSERVATION__OBSERVED,
                            oldObserved, observed));
            }
        }
        return observed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State basicGetObserved() {
        return observed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setObserved(State newObserved) {
        State oldObserved = observed;
        observed = newObserved;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MarkovEntityPackage.OBSERVATION__OBSERVED,
                    oldObserved, observed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case MarkovEntityPackage.OBSERVATION__OBSERVED:
            if (resolve)
                return getObserved();
            return basicGetObserved();
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
        case MarkovEntityPackage.OBSERVATION__OBSERVED:
            setObserved((State) newValue);
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
        case MarkovEntityPackage.OBSERVATION__OBSERVED:
            setObserved((State) null);
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
        case MarkovEntityPackage.OBSERVATION__OBSERVED:
            return observed != null;
        }
        return super.eIsSet(featureID);
    }

} //ObservationImpl
