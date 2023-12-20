/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sample</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl#getReward <em>Reward</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl#getAction <em>Action</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl#getPointInTime <em>Point In Time</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl#getCurrent <em>Current</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl#getNext <em>Next</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl#getObservation <em>Observation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SampleImpl<S, A, R> extends MinimalEObjectImpl.Container implements Sample<S, A, R> {
    /**
     * The cached value of the '{@link #getReward() <em>Reward</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReward()
     * @generated
     * @ordered
     */
    protected Reward<R> reward;

    /**
     * The cached value of the '{@link #getAction() <em>Action</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAction()
     * @generated
     * @ordered
     */
    protected Action<A> action;

    /**
     * The default value of the '{@link #getPointInTime() <em>Point In Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPointInTime()
     * @generated
     * @ordered
     */
    protected static final int POINT_IN_TIME_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPointInTime() <em>Point In Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPointInTime()
     * @generated
     * @ordered
     */
    protected int pointInTime = POINT_IN_TIME_EDEFAULT;

    /**
     * The cached value of the '{@link #getCurrent() <em>Current</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCurrent()
     * @generated
     * @ordered
     */
    protected State<S> current;

    /**
     * The cached value of the '{@link #getNext() <em>Next</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNext()
     * @generated
     * @ordered
     */
    protected State<S> next;

    /**
     * The cached value of the '{@link #getObservation() <em>Observation</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getObservation()
     * @generated
     * @ordered
     */
    protected Observation<S> observation;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SampleImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SampleModelPackage.Literals.SAMPLE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public Reward<R> getReward() {
        if (reward != null && reward.eIsProxy()) {
            InternalEObject oldReward = (InternalEObject) reward;
            reward = (Reward<R>) eResolveProxy(oldReward);
            if (reward != oldReward) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, SampleModelPackage.SAMPLE__REWARD,
                            oldReward, reward));
            }
        }
        return reward;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Reward<R> basicGetReward() {
        return reward;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setReward(Reward<R> newReward) {
        Reward<R> oldReward = reward;
        reward = newReward;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SampleModelPackage.SAMPLE__REWARD, oldReward,
                    reward));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public Action<A> getAction() {
        if (action != null && action.eIsProxy()) {
            InternalEObject oldAction = (InternalEObject) action;
            action = (Action<A>) eResolveProxy(oldAction);
            if (action != oldAction) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, SampleModelPackage.SAMPLE__ACTION,
                            oldAction, action));
            }
        }
        return action;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Action<A> basicGetAction() {
        return action;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setAction(Action<A> newAction) {
        Action<A> oldAction = action;
        action = newAction;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SampleModelPackage.SAMPLE__ACTION, oldAction,
                    action));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getPointInTime() {
        return pointInTime;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPointInTime(int newPointInTime) {
        int oldPointInTime = pointInTime;
        pointInTime = newPointInTime;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SampleModelPackage.SAMPLE__POINT_IN_TIME,
                    oldPointInTime, pointInTime));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public State<S> getCurrent() {
        if (current != null && current.eIsProxy()) {
            InternalEObject oldCurrent = (InternalEObject) current;
            current = (State<S>) eResolveProxy(oldCurrent);
            if (current != oldCurrent) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, SampleModelPackage.SAMPLE__CURRENT,
                            oldCurrent, current));
            }
        }
        return current;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State<S> basicGetCurrent() {
        return current;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setCurrent(State<S> newCurrent) {
        State<S> oldCurrent = current;
        current = newCurrent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SampleModelPackage.SAMPLE__CURRENT, oldCurrent,
                    current));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public State<S> getNext() {
        if (next != null && next.eIsProxy()) {
            InternalEObject oldNext = (InternalEObject) next;
            next = (State<S>) eResolveProxy(oldNext);
            if (next != oldNext) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, SampleModelPackage.SAMPLE__NEXT, oldNext,
                            next));
            }
        }
        return next;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State<S> basicGetNext() {
        return next;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setNext(State<S> newNext) {
        State<S> oldNext = next;
        next = newNext;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SampleModelPackage.SAMPLE__NEXT, oldNext, next));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public Observation<S> getObservation() {
        if (observation != null && observation.eIsProxy()) {
            InternalEObject oldObservation = (InternalEObject) observation;
            observation = (Observation<S>) eResolveProxy(oldObservation);
            if (observation != oldObservation) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, SampleModelPackage.SAMPLE__OBSERVATION,
                            oldObservation, observation));
            }
        }
        return observation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Observation<S> basicGetObservation() {
        return observation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setObservation(Observation<S> newObservation) {
        Observation<S> oldObservation = observation;
        observation = newObservation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SampleModelPackage.SAMPLE__OBSERVATION,
                    oldObservation, observation));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case SampleModelPackage.SAMPLE__REWARD:
            if (resolve)
                return getReward();
            return basicGetReward();
        case SampleModelPackage.SAMPLE__ACTION:
            if (resolve)
                return getAction();
            return basicGetAction();
        case SampleModelPackage.SAMPLE__POINT_IN_TIME:
            return getPointInTime();
        case SampleModelPackage.SAMPLE__CURRENT:
            if (resolve)
                return getCurrent();
            return basicGetCurrent();
        case SampleModelPackage.SAMPLE__NEXT:
            if (resolve)
                return getNext();
            return basicGetNext();
        case SampleModelPackage.SAMPLE__OBSERVATION:
            if (resolve)
                return getObservation();
            return basicGetObservation();
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
        case SampleModelPackage.SAMPLE__REWARD:
            setReward((Reward<R>) newValue);
            return;
        case SampleModelPackage.SAMPLE__ACTION:
            setAction((Action<A>) newValue);
            return;
        case SampleModelPackage.SAMPLE__POINT_IN_TIME:
            setPointInTime((Integer) newValue);
            return;
        case SampleModelPackage.SAMPLE__CURRENT:
            setCurrent((State<S>) newValue);
            return;
        case SampleModelPackage.SAMPLE__NEXT:
            setNext((State<S>) newValue);
            return;
        case SampleModelPackage.SAMPLE__OBSERVATION:
            setObservation((Observation<S>) newValue);
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
        case SampleModelPackage.SAMPLE__REWARD:
            setReward((Reward<R>) null);
            return;
        case SampleModelPackage.SAMPLE__ACTION:
            setAction((Action<A>) null);
            return;
        case SampleModelPackage.SAMPLE__POINT_IN_TIME:
            setPointInTime(POINT_IN_TIME_EDEFAULT);
            return;
        case SampleModelPackage.SAMPLE__CURRENT:
            setCurrent((State<S>) null);
            return;
        case SampleModelPackage.SAMPLE__NEXT:
            setNext((State<S>) null);
            return;
        case SampleModelPackage.SAMPLE__OBSERVATION:
            setObservation((Observation<S>) null);
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
        case SampleModelPackage.SAMPLE__REWARD:
            return reward != null;
        case SampleModelPackage.SAMPLE__ACTION:
            return action != null;
        case SampleModelPackage.SAMPLE__POINT_IN_TIME:
            return pointInTime != POINT_IN_TIME_EDEFAULT;
        case SampleModelPackage.SAMPLE__CURRENT:
            return current != null;
        case SampleModelPackage.SAMPLE__NEXT:
            return next != null;
        case SampleModelPackage.SAMPLE__OBSERVATION:
            return observation != null;
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
        result.append(" (pointInTime: ");
        result.append(pointInTime);
        result.append(')');
        return result.toString();
    }

} //SampleImpl
