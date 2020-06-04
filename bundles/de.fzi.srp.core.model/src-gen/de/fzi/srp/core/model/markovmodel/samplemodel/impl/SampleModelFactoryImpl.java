/**
 */
package de.fzi.srp.core.model.markovmodel.samplemodel.impl;

import de.fzi.srp.core.model.markovmodel.samplemodel.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

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
	public Trajectory createTrajectory() {
		TrajectoryImpl trajectory = new TrajectoryImpl();
		return trajectory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Sample createSample() {
		SampleImpl sample = new SampleImpl();
		return sample;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SampleModel createSampleModel() {
		SampleModelImpl sampleModel = new SampleModelImpl();
		return sampleModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
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
