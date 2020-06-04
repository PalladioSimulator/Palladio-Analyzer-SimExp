/**
 */
package org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModelFactory
 * @model kind="package"
 * @generated
 */
public interface SampleModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "samplemodel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://palladiosimulator.org/markovmodel/samplemodel/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "samplemodel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SampleModelPackage eINSTANCE = org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.TrajectoryImpl <em>Trajectory</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.TrajectoryImpl
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl#getTrajectory()
	 * @generated
	 */
	int TRAJECTORY = 0;

	/**
	 * The feature id for the '<em><b>Sample Path</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAJECTORY__SAMPLE_PATH = 0;

	/**
	 * The number of structural features of the '<em>Trajectory</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAJECTORY_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Trajectory</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAJECTORY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl <em>Sample</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl#getSample()
	 * @generated
	 */
	int SAMPLE = 1;

	/**
	 * The feature id for the '<em><b>Reward</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE__REWARD = 0;

	/**
	 * The feature id for the '<em><b>Action</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE__ACTION = 1;

	/**
	 * The feature id for the '<em><b>Point In Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE__POINT_IN_TIME = 2;

	/**
	 * The feature id for the '<em><b>Current</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE__CURRENT = 3;

	/**
	 * The feature id for the '<em><b>Next</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE__NEXT = 4;

	/**
	 * The feature id for the '<em><b>Observation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE__OBSERVATION = 5;

	/**
	 * The number of structural features of the '<em>Sample</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Sample</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelImpl <em>Sample Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelImpl
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl#getSampleModel()
	 * @generated
	 */
	int SAMPLE_MODEL = 2;

	/**
	 * The feature id for the '<em><b>Trajectories</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE_MODEL__TRAJECTORIES = 0;

	/**
	 * The number of structural features of the '<em>Sample Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE_MODEL_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Sample Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAMPLE_MODEL_OPERATION_COUNT = 0;

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory <em>Trajectory</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Trajectory</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory
	 * @generated
	 */
	EClass getTrajectory();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory#getSamplePath <em>Sample Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sample Path</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Trajectory#getSamplePath()
	 * @see #getTrajectory()
	 * @generated
	 */
	EReference getTrajectory_SamplePath();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample <em>Sample</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sample</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample
	 * @generated
	 */
	EClass getSample();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getReward <em>Reward</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Reward</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getReward()
	 * @see #getSample()
	 * @generated
	 */
	EReference getSample_Reward();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getAction <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Action</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getAction()
	 * @see #getSample()
	 * @generated
	 */
	EReference getSample_Action();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getPointInTime <em>Point In Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Point In Time</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getPointInTime()
	 * @see #getSample()
	 * @generated
	 */
	EAttribute getSample_PointInTime();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getCurrent <em>Current</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Current</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getCurrent()
	 * @see #getSample()
	 * @generated
	 */
	EReference getSample_Current();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getNext <em>Next</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Next</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getNext()
	 * @see #getSample()
	 * @generated
	 */
	EReference getSample_Next();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getObservation <em>Observation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Observation</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample#getObservation()
	 * @see #getSample()
	 * @generated
	 */
	EReference getSample_Observation();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel <em>Sample Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sample Model</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel
	 * @generated
	 */
	EClass getSampleModel();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel#getTrajectories <em>Trajectories</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Trajectories</em>'.
	 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel#getTrajectories()
	 * @see #getSampleModel()
	 * @generated
	 */
	EReference getSampleModel_Trajectories();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SampleModelFactory getSampleModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.TrajectoryImpl <em>Trajectory</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.TrajectoryImpl
		 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl#getTrajectory()
		 * @generated
		 */
		EClass TRAJECTORY = eINSTANCE.getTrajectory();

		/**
		 * The meta object literal for the '<em><b>Sample Path</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRAJECTORY__SAMPLE_PATH = eINSTANCE.getTrajectory_SamplePath();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl <em>Sample</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleImpl
		 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl#getSample()
		 * @generated
		 */
		EClass SAMPLE = eINSTANCE.getSample();

		/**
		 * The meta object literal for the '<em><b>Reward</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SAMPLE__REWARD = eINSTANCE.getSample_Reward();

		/**
		 * The meta object literal for the '<em><b>Action</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SAMPLE__ACTION = eINSTANCE.getSample_Action();

		/**
		 * The meta object literal for the '<em><b>Point In Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SAMPLE__POINT_IN_TIME = eINSTANCE.getSample_PointInTime();

		/**
		 * The meta object literal for the '<em><b>Current</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SAMPLE__CURRENT = eINSTANCE.getSample_Current();

		/**
		 * The meta object literal for the '<em><b>Next</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SAMPLE__NEXT = eINSTANCE.getSample_Next();

		/**
		 * The meta object literal for the '<em><b>Observation</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SAMPLE__OBSERVATION = eINSTANCE.getSample_Observation();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelImpl <em>Sample Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelImpl
		 * @see org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.impl.SampleModelPackageImpl#getSampleModel()
		 * @generated
		 */
		EClass SAMPLE_MODEL = eINSTANCE.getSampleModel();

		/**
		 * The meta object literal for the '<em><b>Trajectories</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SAMPLE_MODEL__TRAJECTORIES = eINSTANCE.getSampleModel_Trajectories();

	}

} //SampleModelPackage
