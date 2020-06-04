/**
 */
package de.fzi.srp.core.model.markovmodel.samplemodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see de.fzi.srp.core.model.markovmodel.samplemodel.SampleModelPackage
 * @generated
 */
public interface SampleModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SampleModelFactory eINSTANCE = de.fzi.srp.core.model.markovmodel.samplemodel.impl.SampleModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Trajectory</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trajectory</em>'.
	 * @generated
	 */
	Trajectory createTrajectory();

	/**
	 * Returns a new object of class '<em>Sample</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sample</em>'.
	 * @generated
	 */
	Sample createSample();

	/**
	 * Returns a new object of class '<em>Sample Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sample Model</em>'.
	 * @generated
	 */
	SampleModel createSampleModel();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	SampleModelPackage getSampleModelPackage();

} //SampleModelFactory
