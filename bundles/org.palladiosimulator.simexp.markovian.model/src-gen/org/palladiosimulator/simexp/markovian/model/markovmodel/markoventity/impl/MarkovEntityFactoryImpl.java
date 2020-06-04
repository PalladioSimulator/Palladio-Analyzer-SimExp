/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MarkovEntityFactoryImpl extends EFactoryImpl implements MarkovEntityFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MarkovEntityFactory init() {
		try {
			MarkovEntityFactory theMarkovEntityFactory = (MarkovEntityFactory) EPackage.Registry.INSTANCE
					.getEFactory(MarkovEntityPackage.eNS_URI);
			if (theMarkovEntityFactory != null) {
				return theMarkovEntityFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MarkovEntityFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MarkovEntityFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case MarkovEntityPackage.MARKOV_MODEL:
			return createMarkovModel();
		case MarkovEntityPackage.STATE:
			return createState();
		case MarkovEntityPackage.OBSERVATION:
			return createObservation();
		case MarkovEntityPackage.TRANSITION:
			return createTransition();
		case MarkovEntityPackage.REWARD:
			return createReward();
		case MarkovEntityPackage.ACTION:
			return createAction();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MarkovModel createMarkovModel() {
		MarkovModelImpl markovModel = new MarkovModelImpl();
		return markovModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public State createState() {
		StateImpl state = new StateImpl();
		return state;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public <O> Observation<O> createObservation() {
		ObservationImpl<O> observation = new ObservationImpl<O>();
		return observation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Transition createTransition() {
		TransitionImpl transition = new TransitionImpl();
		return transition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public <U> Reward<U> createReward() {
		RewardImpl<U> reward = new RewardImpl<U>();
		return reward;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public <T> Action<T> createAction() {
		ActionImpl<T> action = new ActionImpl<T>();
		return action;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MarkovEntityPackage getMarkovEntityPackage() {
		return (MarkovEntityPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MarkovEntityPackage getPackage() {
		return MarkovEntityPackage.eINSTANCE;
	}

} //MarkovEntityFactoryImpl
