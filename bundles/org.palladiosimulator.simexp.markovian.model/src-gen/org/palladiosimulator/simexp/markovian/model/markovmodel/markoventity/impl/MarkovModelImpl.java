/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityPackage;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Markov Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl#getTransitions <em>Transitions</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl#getStateSpace <em>State Space</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl#getReward <em>Reward</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl#getActions <em>Actions</em>}</li>
 *   <li>{@link org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.MarkovModelImpl#getObservations <em>Observations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MarkovModelImpl<T> extends MinimalEObjectImpl.Container implements MarkovModel<T> {
	/**
	 * The cached value of the '{@link #getTransitions() <em>Transitions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransitions()
	 * @generated
	 * @ordered
	 */
	protected EList<Transition<T>> transitions;

	/**
	 * The cached value of the '{@link #getStateSpace() <em>State Space</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStateSpace()
	 * @generated
	 * @ordered
	 */
	protected EList<State<T>> stateSpace;

	/**
	 * The cached value of the '{@link #getReward() <em>Reward</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReward()
	 * @generated
	 * @ordered
	 */
	protected EList<Reward<T>> reward;

	/**
	 * The cached value of the '{@link #getActions() <em>Actions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActions()
	 * @generated
	 * @ordered
	 */
	protected EList<Action<T>> actions;

	/**
	 * The cached value of the '{@link #getObservations() <em>Observations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObservations()
	 * @generated
	 * @ordered
	 */
	protected EList<Observation<T>> observations;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MarkovModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MarkovEntityPackage.Literals.MARKOV_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Transition<T>> getTransitions() {
		if (transitions == null) {
			transitions = new EObjectContainmentEList<Transition<T>>(Transition.class, this,
					MarkovEntityPackage.MARKOV_MODEL__TRANSITIONS);
		}
		return transitions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<State<T>> getStateSpace() {
		if (stateSpace == null) {
			stateSpace = new EObjectContainmentEList<State<T>>(State.class, this,
					MarkovEntityPackage.MARKOV_MODEL__STATE_SPACE);
		}
		return stateSpace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Reward<T>> getReward() {
		if (reward == null) {
			reward = new EObjectContainmentEList<Reward<T>>(Reward.class, this,
					MarkovEntityPackage.MARKOV_MODEL__REWARD);
		}
		return reward;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Action<T>> getActions() {
		if (actions == null) {
			actions = new EObjectContainmentEList<Action<T>>(Action.class, this,
					MarkovEntityPackage.MARKOV_MODEL__ACTIONS);
		}
		return actions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Observation<T>> getObservations() {
		if (observations == null) {
			observations = new EObjectContainmentEList<Observation<T>>(Observation.class, this,
					MarkovEntityPackage.MARKOV_MODEL__OBSERVATIONS);
		}
		return observations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case MarkovEntityPackage.MARKOV_MODEL__TRANSITIONS:
			return ((InternalEList<?>) getTransitions()).basicRemove(otherEnd, msgs);
		case MarkovEntityPackage.MARKOV_MODEL__STATE_SPACE:
			return ((InternalEList<?>) getStateSpace()).basicRemove(otherEnd, msgs);
		case MarkovEntityPackage.MARKOV_MODEL__REWARD:
			return ((InternalEList<?>) getReward()).basicRemove(otherEnd, msgs);
		case MarkovEntityPackage.MARKOV_MODEL__ACTIONS:
			return ((InternalEList<?>) getActions()).basicRemove(otherEnd, msgs);
		case MarkovEntityPackage.MARKOV_MODEL__OBSERVATIONS:
			return ((InternalEList<?>) getObservations()).basicRemove(otherEnd, msgs);
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
		case MarkovEntityPackage.MARKOV_MODEL__TRANSITIONS:
			return getTransitions();
		case MarkovEntityPackage.MARKOV_MODEL__STATE_SPACE:
			return getStateSpace();
		case MarkovEntityPackage.MARKOV_MODEL__REWARD:
			return getReward();
		case MarkovEntityPackage.MARKOV_MODEL__ACTIONS:
			return getActions();
		case MarkovEntityPackage.MARKOV_MODEL__OBSERVATIONS:
			return getObservations();
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
		case MarkovEntityPackage.MARKOV_MODEL__TRANSITIONS:
			getTransitions().clear();
			getTransitions().addAll((Collection<? extends Transition<T>>) newValue);
			return;
		case MarkovEntityPackage.MARKOV_MODEL__STATE_SPACE:
			getStateSpace().clear();
			getStateSpace().addAll((Collection<? extends State<T>>) newValue);
			return;
		case MarkovEntityPackage.MARKOV_MODEL__REWARD:
			getReward().clear();
			getReward().addAll((Collection<? extends Reward<T>>) newValue);
			return;
		case MarkovEntityPackage.MARKOV_MODEL__ACTIONS:
			getActions().clear();
			getActions().addAll((Collection<? extends Action<T>>) newValue);
			return;
		case MarkovEntityPackage.MARKOV_MODEL__OBSERVATIONS:
			getObservations().clear();
			getObservations().addAll((Collection<? extends Observation<T>>) newValue);
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
		case MarkovEntityPackage.MARKOV_MODEL__TRANSITIONS:
			getTransitions().clear();
			return;
		case MarkovEntityPackage.MARKOV_MODEL__STATE_SPACE:
			getStateSpace().clear();
			return;
		case MarkovEntityPackage.MARKOV_MODEL__REWARD:
			getReward().clear();
			return;
		case MarkovEntityPackage.MARKOV_MODEL__ACTIONS:
			getActions().clear();
			return;
		case MarkovEntityPackage.MARKOV_MODEL__OBSERVATIONS:
			getObservations().clear();
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
		case MarkovEntityPackage.MARKOV_MODEL__TRANSITIONS:
			return transitions != null && !transitions.isEmpty();
		case MarkovEntityPackage.MARKOV_MODEL__STATE_SPACE:
			return stateSpace != null && !stateSpace.isEmpty();
		case MarkovEntityPackage.MARKOV_MODEL__REWARD:
			return reward != null && !reward.isEmpty();
		case MarkovEntityPackage.MARKOV_MODEL__ACTIONS:
			return actions != null && !actions.isEmpty();
		case MarkovEntityPackage.MARKOV_MODEL__OBSERVATIONS:
			return observations != null && !observations.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //MarkovModelImpl
