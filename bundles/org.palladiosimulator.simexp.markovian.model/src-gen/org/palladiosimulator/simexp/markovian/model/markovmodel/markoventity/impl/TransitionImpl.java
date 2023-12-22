/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.TransitionImpl#getProbability <em>Probability</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransitionImpl<S, A> extends MinimalEObjectImpl.Container implements Transition<S, A> {
    /**
     * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSource()
     * @generated
     * @ordered
     */
    protected State<S> source;

    /**
     * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected State<S> target;

    /**
     * The cached value of the '{@link #getLabel() <em>Label</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected Action<A> label;

    /**
     * The default value of the '{@link #getProbability() <em>Probability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProbability()
     * @generated
     * @ordered
     */
    protected static final double PROBABILITY_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getProbability() <em>Probability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProbability()
     * @generated
     * @ordered
     */
    protected double probability = PROBABILITY_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TransitionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MarkovEntityPackage.Literals.TRANSITION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public State<S> getSource() {
        if (source != null && source.eIsProxy()) {
            InternalEObject oldSource = (InternalEObject) source;
            source = (State<S>) eResolveProxy(oldSource);
            if (source != oldSource) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, MarkovEntityPackage.TRANSITION__SOURCE,
                            oldSource, source));
            }
        }
        return source;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State<S> basicGetSource() {
        return source;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setSource(State<S> newSource) {
        State<S> oldSource = source;
        source = newSource;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MarkovEntityPackage.TRANSITION__SOURCE, oldSource,
                    source));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public State<S> getTarget() {
        if (target != null && target.eIsProxy()) {
            InternalEObject oldTarget = (InternalEObject) target;
            target = (State<S>) eResolveProxy(oldTarget);
            if (target != oldTarget) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, MarkovEntityPackage.TRANSITION__TARGET,
                            oldTarget, target));
            }
        }
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State<S> basicGetTarget() {
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setTarget(State<S> newTarget) {
        State<S> oldTarget = target;
        target = newTarget;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MarkovEntityPackage.TRANSITION__TARGET, oldTarget,
                    target));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public Action<A> getLabel() {
        if (label != null && label.eIsProxy()) {
            InternalEObject oldLabel = (InternalEObject) label;
            label = (Action<A>) eResolveProxy(oldLabel);
            if (label != oldLabel) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, MarkovEntityPackage.TRANSITION__LABEL,
                            oldLabel, label));
            }
        }
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Action<A> basicGetLabel() {
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setLabel(Action<A> newLabel) {
        Action<A> oldLabel = label;
        label = newLabel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MarkovEntityPackage.TRANSITION__LABEL, oldLabel,
                    label));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getProbability() {
        return probability;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setProbability(double newProbability) {
        double oldProbability = probability;
        probability = newProbability;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MarkovEntityPackage.TRANSITION__PROBABILITY,
                    oldProbability, probability));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case MarkovEntityPackage.TRANSITION__SOURCE:
            if (resolve)
                return getSource();
            return basicGetSource();
        case MarkovEntityPackage.TRANSITION__TARGET:
            if (resolve)
                return getTarget();
            return basicGetTarget();
        case MarkovEntityPackage.TRANSITION__LABEL:
            if (resolve)
                return getLabel();
            return basicGetLabel();
        case MarkovEntityPackage.TRANSITION__PROBABILITY:
            return getProbability();
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
        case MarkovEntityPackage.TRANSITION__SOURCE:
            setSource((State<S>) newValue);
            return;
        case MarkovEntityPackage.TRANSITION__TARGET:
            setTarget((State<S>) newValue);
            return;
        case MarkovEntityPackage.TRANSITION__LABEL:
            setLabel((Action<A>) newValue);
            return;
        case MarkovEntityPackage.TRANSITION__PROBABILITY:
            setProbability((Double) newValue);
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
        case MarkovEntityPackage.TRANSITION__SOURCE:
            setSource((State<S>) null);
            return;
        case MarkovEntityPackage.TRANSITION__TARGET:
            setTarget((State<S>) null);
            return;
        case MarkovEntityPackage.TRANSITION__LABEL:
            setLabel((Action<A>) null);
            return;
        case MarkovEntityPackage.TRANSITION__PROBABILITY:
            setProbability(PROBABILITY_EDEFAULT);
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
        case MarkovEntityPackage.TRANSITION__SOURCE:
            return source != null;
        case MarkovEntityPackage.TRANSITION__TARGET:
            return target != null;
        case MarkovEntityPackage.TRANSITION__LABEL:
            return label != null;
        case MarkovEntityPackage.TRANSITION__PROBABILITY:
            return probability != PROBABILITY_EDEFAULT;
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
        result.append(" (probability: ");
        result.append(probability);
        result.append(')');
        return result.toString();
    }

} //TransitionImpl
