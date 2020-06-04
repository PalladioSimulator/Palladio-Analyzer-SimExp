/**
 */
package de.fzi.srp.core.model.markovmodel.markoventity.impl;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityPackage;
import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.impl.TransitionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.impl.TransitionImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.impl.TransitionImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link de.fzi.srp.core.model.markovmodel.markoventity.impl.TransitionImpl#getProbability <em>Probability</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransitionImpl extends MinimalEObjectImpl.Container implements Transition {
	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected State source;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected State target;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected Action label;

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
	public State getSource() {
		if (source != null && source.eIsProxy()) {
			InternalEObject oldSource = (InternalEObject) source;
			source = (State) eResolveProxy(oldSource);
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
	public State basicGetSource() {
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSource(State newSource) {
		State oldSource = source;
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
	public State getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject) target;
			target = (State) eResolveProxy(oldTarget);
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
	public State basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTarget(State newTarget) {
		State oldTarget = target;
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
	public Action getLabel() {
		if (label != null && label.eIsProxy()) {
			InternalEObject oldLabel = (InternalEObject) label;
			label = (Action) eResolveProxy(oldLabel);
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
	public Action basicGetLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(Action newLabel) {
		Action oldLabel = label;
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
	public double getProbability() {
		return probability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
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
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case MarkovEntityPackage.TRANSITION__SOURCE:
			setSource((State) newValue);
			return;
		case MarkovEntityPackage.TRANSITION__TARGET:
			setTarget((State) newValue);
			return;
		case MarkovEntityPackage.TRANSITION__LABEL:
			setLabel((Action) newValue);
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
			setSource((State) null);
			return;
		case MarkovEntityPackage.TRANSITION__TARGET:
			setTarget((State) null);
			return;
		case MarkovEntityPackage.TRANSITION__LABEL:
			setLabel((Action) null);
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
