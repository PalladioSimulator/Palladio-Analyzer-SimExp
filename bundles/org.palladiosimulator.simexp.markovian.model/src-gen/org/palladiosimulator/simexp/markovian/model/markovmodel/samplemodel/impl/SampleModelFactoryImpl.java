/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SampleModelFactoryImpl extends EFactoryImpl implements SampleModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static SampleModelFactory init() {
		try {
			SampleModelFactory theSampleModelFactory = (SampleModelFactory) EPackage.Registry.INSTANCE
					.getEFactory(SampleModelPackage.eNS_URI);
			if (theSampleModelFactory != null) {
				return theSampleModelFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new SampleModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SampleModelFactoryImpl() {
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
		case SampleModelPackage.TRAJECTORY:
			return createTrajectory();
		case SampleModelPackage.SAMPLE:
			return createSample();
		case SampleModelPackage.SAMPLE_MODEL:
			return createSampleModel();
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
	public <T> Trajectory<T> createTrajectory() {
		TrajectoryImpl<T> trajectory = new TrajectoryImpl<T>();
		return trajectory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public <T> Sample<T> createSample() {
		SampleImpl<T> sample = new SampleImpl<T>();
		return sample;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public <T> SampleModel<T> createSampleModel() {
		SampleModelImpl<T> sampleModel = new SampleModelImpl<T>();
		return sampleModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SampleModelPackage getSampleModelPackage() {
		return (SampleModelPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static SampleModelPackage getPackage() {
		return SampleModelPackage.eINSTANCE;
	}

} //SampleModelFactoryImpl
